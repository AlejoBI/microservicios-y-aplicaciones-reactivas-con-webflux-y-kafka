package com.example._3_microservciocio_crud_reactivo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table("productos")
public class Producto {
    @Id
    private int codProducto;
    private String nombre;
    private String categoria;
    private double precioProducto;
    private int stock;
}
