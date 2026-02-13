package com.example.epg.HEXAGONAL.infrastructure.adapter.in.rest;

import com.example.epg.HEXAGONAL.domain.port.in.ConsultarSaldoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * ADAPTADOR DE ENTRADA (INPUT ADAPTER) - REST Controller
 *
 * En arquitectura hexagonal:
 * - Es un ADAPTADOR que convierte peticiones HTTP al dominio
 * - DEPENDE del puerto de entrada (ConsultarSaldoUseCase)
 * - NO contiene lógica de negocio
 * - Traduce HTTP → Dominio
 *
 * SOLID aplicado:
 * - Single Responsibility: Solo maneja la entrada HTTP
 * - Dependency Inversion: Depende de la abstracción (UseCase), no implementación
 * - Open/Closed: Podemos agregar más endpoints sin modificar el caso de uso
 *
 * Flujo:
 * Controller (HTTP) → UseCase (dominio) → Puertos OUT (BD, Cache)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/saldos")
@RequiredArgsConstructor
public class SaldoController {

    // Dependemos del puerto de entrada (abstracción)
    private final ConsultarSaldoUseCase consultarSaldoUseCase;

    /**
     * Endpoint para consultar saldo
     *
     * GET /api/v1/saldos/{numeroCuenta}
     *
     * @param numeroCuenta Número de cuenta a consultar
     * @return Mono<SaldoResponse> DTO con el saldo
     */
    @GetMapping("/{numeroCuenta}")
    public Mono<SaldoResponse> obtenerSaldo(@PathVariable String numeroCuenta) {
        log.info("📥 Petición recibida: consultar saldo para cuenta {}", numeroCuenta);

        return consultarSaldoUseCase.consultarSaldo(numeroCuenta)
                .map(saldo -> new SaldoResponse(
                        saldo.getNumeroCuenta(),
                        saldo.getMonto()
                ))
                .doOnSuccess(response ->
                        log.info("📤 Respuesta enviada para cuenta {}: monto {}",
                                numeroCuenta, response.getMonto())
                );
    }

    /**
     * DTO de respuesta (Response)
     * Se define aquí porque es parte de la capa de presentación
     */
    public record SaldoResponse(
            String numeroCuenta,
            double monto
    ) {
        public double getMonto() {
            return monto;
        }
    }
}
