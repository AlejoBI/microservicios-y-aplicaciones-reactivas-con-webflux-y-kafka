package com.example._5_test_crud_productos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Producto {
    private int codProducto;
    private String nombre;
    private String categoria;
    private double precioProducto;
    private int stock;
}
