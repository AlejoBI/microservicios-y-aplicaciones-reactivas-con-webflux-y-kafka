package com.example._6_microservicio_rastreador.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Elemento {
    private String nombre;
    private String categoria;
    private double precioProducto;
    private String tienda;
}
