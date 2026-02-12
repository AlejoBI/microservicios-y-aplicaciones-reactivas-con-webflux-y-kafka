package com.example._3_microservciocio_crud_reactivo.repository;

import com.example._3_microservciocio_crud_reactivo.model.Producto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductosRepository extends ReactiveCrudRepository<Producto, Integer> {
    Flux<Producto> findByCategoria(String categoria);

    Mono<Void> deleteByNombre(String nombre);

    @Query("DELETE FROM productos WHERE precio_producto < :precioMax")
    Mono<Void> deleteByPrecioProductoLessThan(double precioMax);
}
