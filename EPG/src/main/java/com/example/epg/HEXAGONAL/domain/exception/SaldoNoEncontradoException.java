package com.example.epg.HEXAGONAL.domain.exception;

/**
 * EXCEPCIÓN DE DOMINIO
 *
 * En arquitectura hexagonal, las excepciones de negocio viven en el dominio.
 * Características:
 * - Representa un error del negocio, no técnico
 * - NO depende de frameworks
 * - Se lanza desde el dominio o casos de uso
 *
 * SOLID aplicado:
 * - Single Responsibility: Solo representa el error "Saldo no encontrado"
 */
public class SaldoNoEncontradoException extends RuntimeException {

    private final String numeroCuenta;

    public SaldoNoEncontradoException(String numeroCuenta) {
        super(String.format("No se encontró saldo para la cuenta: %s", numeroCuenta));
        this.numeroCuenta = numeroCuenta;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }
}
