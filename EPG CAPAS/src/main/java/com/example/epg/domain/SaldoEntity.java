package com.example.epg.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "saldos")
public class SaldoEntity {

    @Id
    private Long id;
    private String numeroCuenta;
    private Double monto;
}
