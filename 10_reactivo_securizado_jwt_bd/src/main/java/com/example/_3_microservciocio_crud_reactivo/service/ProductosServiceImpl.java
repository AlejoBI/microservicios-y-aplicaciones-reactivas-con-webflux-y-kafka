package com.example._3_microservciocio_crud_reactivo.service;

import com.example._3_microservciocio_crud_reactivo.model.Producto;
import com.example._3_microservciocio_crud_reactivo.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductosServiceImpl implements ProductosService {
    @Autowired
    ProductosRepository productosRepository;

    @Override
    public Flux<Producto> catalogo() {
        return productosRepository.findAll()
                .delayElements(Duration.ofSeconds(2));
    }

    @Override
    public Flux<Producto> productosCategoria(String categoria) {
        return productosRepository.findByCategoria(categoria);
    }

    @Override
    public Mono<Producto> productoCodigo(int cod) {
        return productosRepository.findById(cod);
    }

    @Override
    public Mono<Void> altaProducto(Producto producto) {
        return productoCodigo(producto.getCodProducto())
                .switchIfEmpty(Mono.just(producto)
                        .flatMap(prod -> productosRepository.save(prod)))
                .then(); // Devuelve Mono<Void> indicando que la operación ha finalizado
    }

    @Override
    public Mono<Producto> eliminarProducto(int cod) {
        return productoCodigo(cod)
                .flatMap(producto -> productosRepository.findById(cod) // Mono<Void>
                        .then(Mono.just(producto))); // Mono<Producto>
    }

    @Override
    public Mono<Producto> actualizarPrecio(int cod, double precio) {
        return productoCodigo(cod)
                .flatMap(producto -> {
                    producto.setPrecioProducto(precio);
                    return productosRepository.save(producto); // Mono<Producto>
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado")));
    }

}
