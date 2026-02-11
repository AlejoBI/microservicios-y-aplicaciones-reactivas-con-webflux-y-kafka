package com.example._3_microservciocio_crud_reactivo.service;

import com.example._3_microservciocio_crud_reactivo.model.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductosService {
    public abstract Flux<Producto> catalogo();

    public abstract Flux<Producto> productosCategoria(String categoria);

    public abstract Mono<Producto> productoCodigo(int cod);

    public abstract Mono<Void> altaProducto(Producto producto);

    public abstract Mono<Producto> eliminarProducto(int cod);

    public abstract Mono<Producto> actualizarPrecio(int cod, double precio);
}
