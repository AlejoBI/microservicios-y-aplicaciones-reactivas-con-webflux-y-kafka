package com.example.epg.HEXAGONAL.application.usecase;

import com.example.epg.HEXAGONAL.domain.exception.SaldoNoEncontradoException;
import com.example.epg.HEXAGONAL.domain.model.Saldo;
import com.example.epg.HEXAGONAL.domain.port.in.ConsultarSaldoUseCase;
import com.example.epg.HEXAGONAL.domain.port.out.SaldoCachePort;
import com.example.epg.HEXAGONAL.domain.port.out.SaldoRepositoryPort;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * CASO DE USO (USE CASE) - Implementación del puerto de entrada
 * <p>
 * En arquitectura hexagonal:
 * - IMPLEMENTA el puerto de entrada (ConsultarSaldoUseCase)
 * - Contiene la LÓGICA DE APLICACIÓN (orquestación)
 * - USA los puertos de salida (SaldoRepositoryPort, SaldoCachePort)
 * - NO conoce detalles de infraestructura (BD, Redis, HTTP)
 * <p>
 * SOLID aplicado:
 * - Single Responsibility: Solo orquesta la consulta de saldo
 * - Dependency Inversion: Depende de abstracciones (puertos), no implementaciones
 * - Open/Closed: Abierto a extensión (nuevos puertos), cerrado a modificación
 * <p>
 * Flujo:
 * 1. Intenta obtener del cache
 * 2. Si no está en cache, busca en BD
 * 3. Si encuentra en BD, lo guarda en cache
 * 4. Aplica timeout, retry y circuit breaker
 * 5. Si falla todo, activa fallback
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultarSaldoUseCaseImpl implements ConsultarSaldoUseCase {

    // Puertos de salida (abstracciones)
    private final SaldoRepositoryPort saldoRepository;
    private final SaldoCachePort cachePort;

    // Resiliencia
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final MeterRegistry meterRegistry;

    private static final String CIRCUIT_BREAKER_NAME = "saldoService";

    @Override
    public Mono<Saldo> consultarSaldo(String numeroCuenta) {
        long inicio = System.currentTimeMillis();

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME);
        Timer.Sample sample = Timer.start(meterRegistry);

        return Mono.defer(() ->
                        ejecutarConsulta(numeroCuenta)
                                // TIMEOUT: Protección contra llamadas lentas
                                .timeout(Duration.ofSeconds(1))

                                // RETRY: Reintentos para errores transitorios
                                .retryWhen(
                                        Retry.backoff(2, Duration.ofMillis(50))
                                                .filter(this::esErrorTransitorio)
                                )

                                // CIRCUIT BREAKER: Protección contra fallos en cascada
                                .transformDeferred(CircuitBreakerOperator.of(cb))

                                // FALLBACK: Respuesta por defecto si falla todo
                                .onErrorResume(ex -> {
                                    log.warn("Fallback activado para cuenta {} debido a: {}",
                                            numeroCuenta, ex.toString());
                                    return fallbackSeguro(numeroCuenta);
                                })
                )
                .doOnSuccess(saldo -> {
                    long fin = System.currentTimeMillis();
                    log.info("✅ Saldo consultado para cuenta {} en {} ms",
                            numeroCuenta, (fin - inicio));
                })
                .doFinally(signal -> {
                    String resultado = signal == reactor.core.publisher.SignalType.ON_COMPLETE
                            ? "OK" : "ERROR";
                    sample.stop(
                            Timer.builder("saldo.consulta.latencia")
                                    .tag("resultado", resultado)
                                    .register(meterRegistry)
                    );
                });
    }

    /**
     * Lógica principal de consulta con cache-aside pattern
     */
    private Mono<Saldo> ejecutarConsulta(String numeroCuenta) {
        return cachePort.obtener(numeroCuenta)
                .doOnNext(saldo -> log.info("🔵 Cache HIT para cuenta {}", numeroCuenta))
                .switchIfEmpty(
                        saldoRepository.findByNumeroCuenta(numeroCuenta)
                                .doOnNext(saldo -> log.info("🟢 BD HIT para cuenta {}", numeroCuenta))
                                .switchIfEmpty(Mono.error(
                                        new SaldoNoEncontradoException(numeroCuenta)
                                ))
                                .flatMap(saldo ->
                                        cachePort.guardar(numeroCuenta, saldo)
                                                .thenReturn(saldo)
                                )
                );
    }

    /**
     * Fallback seguro: devuelve un saldo en cero
     */
    private Mono<Saldo> fallbackSeguro(String numeroCuenta) {
        log.warn("🔴 Fallback activado: devolviendo saldo 0 para cuenta {}", numeroCuenta);
        return Mono.just(new Saldo(numeroCuenta, 0.0));
    }

    /**
     * Determina si un error es transitorio y puede reintentarse
     */
    private boolean esErrorTransitorio(Throwable ex) {
        return ex instanceof TimeoutException
                || ex instanceof IOException;
    }
}
