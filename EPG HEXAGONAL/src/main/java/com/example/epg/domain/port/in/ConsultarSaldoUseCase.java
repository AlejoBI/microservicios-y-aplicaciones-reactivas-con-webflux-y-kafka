package com.example.epg.HEXAGONAL.domain.port.in;

import com.example.epg.HEXAGONAL.domain.model.Saldo;
import reactor.core.publisher.Mono;

/**
 * PUERTO DE ENTRADA (INPUT PORT / USE CASE)
 *
 * En arquitectura hexagonal:
 * - Define QUÉ puede hacer la aplicación (casos de uso)
 * - Es una INTERFAZ (abstracción)
 * - La implementación está en la capa de APPLICATION
 * - El dominio NO depende de la implementación
 *
 * SOLID aplicado:
 * - Dependency Inversion: Dependemos de abstracción, no implementación
 * - Interface Segregation: Interface específica para consultar saldo
 * - Single Responsibility: Solo define el caso de uso "Consultar Saldo"
 *
 * Ejemplo de uso:
 * - El Controller (adaptador IN) llama a este puerto
 * - La implementación (UseCase) orquesta la lógica
 */
public interface ConsultarSaldoUseCase {

    /**
     * Caso de uso: Consultar el saldo de una cuenta
     *
     * @param numeroCuenta Número de cuenta a consultar
     * @return Mono con el saldo encontrado
     * @throws com.example.epg.HEXAGONAL.domain.exception.SaldoNoEncontradoException si no existe
     */
    Mono<Saldo> consultarSaldo(String numeroCuenta);
}
