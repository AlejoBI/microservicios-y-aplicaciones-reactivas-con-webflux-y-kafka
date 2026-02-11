package com.example._1_microservicio_crud_productos.service;

import com.example._1_microservicio_crud_productos.model.Producto;

import java.util.List;

public abstract class ProductoService {
    public abstract List<Producto> catalogo();

    public abstract List<Producto> productosCategoria(String categoria);

    public abstract Producto productoCodigo(int cod);

    public abstract void altaProducto(Producto producto);

    public abstract Producto eliminarProducto(int cod);

    public abstract Producto actualizarPrecio(int cod, double precio);
}
