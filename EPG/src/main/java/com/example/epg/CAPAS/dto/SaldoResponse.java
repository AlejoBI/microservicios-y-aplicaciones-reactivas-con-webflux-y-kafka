package com.example.epg.CAPAS.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SaldoResponse {

    private final String numeroCuenta;
    private final double monto;
}
