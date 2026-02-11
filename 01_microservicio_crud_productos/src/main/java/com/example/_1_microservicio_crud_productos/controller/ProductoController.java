package com.example._1_microservicio_crud_productos.controller;

import com.example._1_microservicio_crud_productos.model.Producto;
import com.example._1_microservicio_crud_productos.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {
    @Autowired
    ProductoService productoService;

    @GetMapping(value = "/", produces = "application/json")
    public List<Producto> productos() {
        return productoService.catalogo();
    }

    @GetMapping(value = "/categoria/{categoria}", produces = "application/json")
    public List<Producto> productosCategoria(@PathVariable("categoria") String categoria) {
        return productoService.productosCategoria(categoria);
    }

    @GetMapping(value = "/codigo/{cod}", produces = "application/json")
    public Producto productoCodigo(@PathVariable("cod") int cod) {
        return productoService.productoCodigo(cod);
    }

    @PostMapping(value = "/alta", produces = "application/json")
    public void altaProducto(@RequestBody Producto producto) {
        productoService.altaProducto(producto);
    }

    @DeleteMapping(value = "/eliminar", produces = "application/json")
    public Producto eliminarProducto(@RequestParam("cod") int cod) {
        return productoService.eliminarProducto(cod);
    }

    @PutMapping(value = "/actualizar", produces = "application/json")
    public Producto actualizarPrecio(@RequestParam("cod") int cod, @RequestParam("precio") double precio) {
        return productoService.actualizarPrecio(cod, precio);
    }
}
