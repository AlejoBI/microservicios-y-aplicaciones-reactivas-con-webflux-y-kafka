package com.example._3_microservciocio_crud_reactivo.service;

import com.example._3_microservciocio_crud_reactivo.model.Producto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductosServiceImpl implements ProductosService {
    private static List<Producto> productos = new ArrayList<>(List.of(
            new Producto(4, "Gorra", "Accesorios", 14.99, 200),
            new Producto(5, "Bolso", "Accesorios", 49.99, 20),
            new Producto(6, "Chaqueta", "Ropa", 89.99, 10),
            new Producto(7, "Sandalias", "Calzado", 29.99, 40)
    ));

    @Override
    public Flux<Producto> catalogo() {
        return Flux.fromIterable(productos)
                .delayElements(Duration.ofSeconds(2));
    }

    @Override
    public Flux<Producto> productosCategoria(String categoria) {
        return Flux.fromIterable(productos)
                .filter(p -> p.getCategoria().equals(categoria));
    }

    @Override
    public Mono<Producto> productoCodigo(int cod) {
        return Flux.fromIterable(productos)
                .filter(p -> p.getCodProducto() == cod)
                .next(); // Devuelve el primer elemento que cumpla la condición o Mono.empty() si no se encuentra ninguno
        //.switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado")));
    }

    @Override
    public Mono<Void> altaProducto(Producto producto) {
        return productoCodigo(producto.getCodProducto())
                .switchIfEmpty(Mono.just(producto)
                        .map(prod -> {
                            productos.add(prod);
                            return prod;
                        }))
                .then(); // Devuelve Mono<Void> indicando que la operación ha finalizado
    }

    @Override
    public Mono<Producto> eliminarProducto(int cod) {
        return productoCodigo(cod)
                .map(producto -> {
                    productos.removeIf(p -> p.getCodProducto() == cod);
                    return producto;
                }); // Mono<Producto> con el producto eliminado o Mono.empty() si no se encuentra el producto
                //.switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado")));
    }

    @Override
    public Mono<Producto> actualizarPrecio(int cod, double precio) {
        return productoCodigo(cod)
                .map(producto -> {
                    producto.setPrecioProducto(precio);
                    return producto;
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado")));
    }

}
