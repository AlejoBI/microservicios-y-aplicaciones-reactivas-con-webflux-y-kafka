package com.example._1_microservicio_crud_productos.service;

import com.example._1_microservicio_crud_productos.model.Producto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl extends ProductoService {

    private static List<Producto> productos = new ArrayList<>(List.of(
            new Producto(1, "Camiseta", "Ropa", 19.99, 100),
            new Producto(2, "Pantalón", "Ropa", 39.99, 50),
            new Producto(3, "Zapatos", "Calzado", 59.99, 30)
    ));

    @Override
    public List<Producto> catalogo() {
        return productos;
    }

    @Override
    public List<Producto> productosCategoria(String categoria) {
        return productos.stream()
                .filter(p -> p.getCategoria().equals(categoria))
                .collect(Collectors.toList());
    }

    @Override
    public Producto productoCodigo(int cod) {
        return productos.stream()
                .filter(p -> p.getCodProducto() == cod)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void altaProducto(Producto producto) {
        if (productoCodigo(producto.getCodProducto()) == null) {
            productos.add(producto);
        }
    }

    @Override
    public Producto eliminarProducto(int cod) {
        Producto producto = productoCodigo(cod);
        if (producto != null) {
            productos.removeIf(p -> p.getCodProducto() == cod);
        }
        return producto;
    }

    @Override
    public Producto actualizarPrecio(int cod, double precio) {
        Producto producto = productoCodigo(cod);
        if (producto != null) {
            producto.setPrecioUnitario(precio);
        }
        return producto;
    }
}
