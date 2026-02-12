package com.example._3_microservciocio_crud_reactivo.controller;

import com.example._3_microservciocio_crud_reactivo.model.Producto;
import com.example._3_microservciocio_crud_reactivo.service.ProductosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/productos")
public class ProductosController {
    @Autowired
    ProductosService productosService;

    @GetMapping(value = "/")
    public ResponseEntity<Flux<Producto>> productos() {
        return new ResponseEntity<>(productosService.catalogo(), HttpStatus.OK);
    }

    @GetMapping(value = "/categoria")
    public ResponseEntity<Flux<Producto>> productosCategoria(@RequestParam String categoria) {
        return new ResponseEntity<>(productosService.productosCategoria(categoria), HttpStatus.OK);
    }

    @GetMapping(value = "/{cod}")
    public ResponseEntity<Mono<Producto>> productoCodigo(@PathVariable int cod) {
        return new ResponseEntity<>(productosService.productoCodigo(cod), HttpStatus.OK);
    }

    @PostMapping(value = "/", produces = "application/json")
    public ResponseEntity<Mono<Void>> altaProducto(@RequestBody Producto producto) {
        return new ResponseEntity<>(productosService.altaProducto(producto), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{cod}")
    public Mono<ResponseEntity<Producto>> eliminarProducto(@PathVariable int cod) {
        return productosService.eliminarProducto(cod)
                .map(producto -> new ResponseEntity<>(producto, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Si el producto no se encuentra, devuelve 404 Not Found
    }

    @PutMapping(value = "/{cod}")
    public Mono<ResponseEntity<Producto>> actualizarPrecio(@PathVariable int cod, @RequestParam double precio) {
        return productosService.actualizarPrecio(cod, precio)
                .map(producto -> new ResponseEntity<>(producto, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}