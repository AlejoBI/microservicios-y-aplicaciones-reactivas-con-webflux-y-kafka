package com.example.epg.HEXAGONAL.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * MODELO DE DOMINIO (DOMAIN MODEL)
 *
 * En arquitectura hexagonal, esta es la entidad del NÚCLEO (core).
 * Características:
 * - NO tiene dependencias de frameworks (ni JPA, ni Spring, ni Redis)
 * - Contiene la lógica de negocio pura
 * - Es inmutable (final fields, sin setters)
 * - Representa conceptos del negocio, no tablas de BD
 *
 * SOLID aplicado:
 * - Single Responsibility: Solo representa el concepto de "Saldo"
 * - Open/Closed: Se puede extender sin modificar
 */
@AllArgsConstructor
@Getter
public class Saldo {

    private final String numeroCuenta;
    private final double monto;

    /**
     * Método de dominio: validación de negocio
     * La lógica de negocio vive en el dominio, no en servicios
     */
    public boolean tieneFondos() {
        return monto > 0;
    }

    /**
     * Método de dominio: operación de negocio
     */
    public Saldo aplicarDescuento(double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("Porcentaje inválido");
        }
        double nuevoMonto = monto - (monto * porcentaje / 100);
        return new Saldo(numeroCuenta, nuevoMonto);
    }
}
