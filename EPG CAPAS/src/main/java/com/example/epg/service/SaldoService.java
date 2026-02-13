package com.example.epg.service;

import com.example.epg.domain.SaldoEntity;
import com.example.epg.dto.SaldoResponse;
import com.example.epg.exception.SaldoNoEncontradoException;
import com.example.epg.repository.SaldoRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class SaldoService {

    private final SaldoRepository saldoRepository;
    private final SaldoCacheService cacheService;
    // Inyección del CircuitBreakerRegistry para obtener instancias de Circuit Breaker
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    // Inyección del MeterRegistry para métricas (adicional si se requiere)
    private final MeterRegistry meterRegistry;

    // Nombre de la instancia configurada en application.properties
    private static final String CIRCUIT_BREAKER_NAME = "saldoService";

    public Mono<SaldoResponse> consultarSaldo(String numeroCuenta) {
        long inicio = System.currentTimeMillis();

        // Obtener la instancia del Circuit Breaker
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME);

        Timer.Sample sample = Timer.start(meterRegistry);

        return Mono.defer(
                        () -> flujoConsultarSaldo(numeroCuenta)
                                // Timeout (protege cache y BD)
                                .timeout(Duration.ofSeconds(1))
                                // Reintentos para errores transitorios
                                .retryWhen(
                                        Retry.backoff(2, Duration.ofMillis(50))
                                                .filter(this::esErrorTransitorio)
                                )
                                // Circuit Breaker observa todo el flujo
                                .transformDeferred(CircuitBreakerOperator.of(cb))
                                .onErrorResume(ex -> {
                                    log.warn("Fallback activado para cuenta {} debido a: {}", numeroCuenta, ex.toString());
                                    return fallbackSeguro(numeroCuenta);
                                }))
                .doOnSuccess(r -> {
                    long fin = System.currentTimeMillis();
                    log.info("Tiempo de respuesta total para cuenta {}: {} ms", numeroCuenta, (fin - inicio));
                })
                .doFinally(signal -> {
                    String resultado = signal == reactor.core.publisher.SignalType.ON_COMPLETE ? "OK" : "ERROR";
                    sample.stop(
                            Timer.builder("saldo.consulta.latencia")
                                    .tag("resultado", resultado)
                                    .register(meterRegistry)
                    );
                });
    }

    public Mono<SaldoResponse> flujoConsultarSaldo(String numeroCuenta) {
        return cacheService.obtener(numeroCuenta)
                .switchIfEmpty(
                        saldoRepository.findByNumeroCuenta(numeroCuenta)
                                .switchIfEmpty(Mono.error(new SaldoNoEncontradoException(numeroCuenta)))
                                .map(this::mapToResponse)
                                .flatMap(response ->
                                        cacheService.guardar(numeroCuenta, response)
                                                .thenReturn(response)
                                )
                );
    }

    private SaldoResponse mapToResponse(SaldoEntity entity) {
        return new SaldoResponse(entity.getNumeroCuenta(), entity.getMonto());
    }

    private Mono<SaldoResponse> fallbackSeguro(String cuenta) {
        return Mono.just(new SaldoResponse(cuenta, 0.0));
    }

    private boolean esErrorTransitorio(Throwable ex) {
        return ex instanceof TimeoutException
                || ex instanceof IOException;
    }

}
