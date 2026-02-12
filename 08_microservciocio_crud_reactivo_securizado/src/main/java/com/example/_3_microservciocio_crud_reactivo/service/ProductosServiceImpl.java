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
            new Producto(1, "Camiseta", "Ropa", 19.99, 100),
            new Producto(2, "Pantalón", "Ropa", 39.99, 50),
            new Producto(3, "Zapatos", "Calzado", 59.99, 30)
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
