package com.example._3_microservciocio_crud_reactivo;

import com.example._3_microservciocio_crud_reactivo.model.Producto;
import com.example._3_microservciocio_crud_reactivo.service.ProductosService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApplicationTests {
    @Autowired
    ProductosService productosService;

    @Test
    @Order(1)
    void testProductosCategoria() {
        StepVerifier.create(productosService.productosCategoria("Ropa"))
                .expectNextMatches(producto -> producto.getNombre().equals("Camiseta"))
                .expectNextMatches(producto -> producto.getNombre().equals("Pantalón"))
                .verifyComplete();
    }

    @Test
    @Order(2)
    void testEliminarProducto() {
        StepVerifier.create(productosService.eliminarProducto(1))
                .expectNextMatches(producto -> producto.getNombre().equals("Camiseta"))
                .verifyComplete();
        StepVerifier.create(productosService.productoCodigo(1))
                .expectNextCount(0) // No se espera ningún producto con el código 1
                .verifyComplete();
    }

    @Test
    @Order(3)
    void testAltaProducto() {
        StepVerifier.create(productosService.altaProducto(new Producto(4, "Gorra", "Accesorios", 14.99, 20)))
                .expectComplete()
                .verify();
        StepVerifier.create(productosService.productoCodigo(4))
                .expectNextMatches(producto -> producto.getNombre().equals("Gorra"))
                .verifyComplete();
    }

    @Test
    @Order(4)
    void testCatalogo() {
        StepVerifier.create(productosService.catalogo())
                .expectNextCount(3) // Se esperan 3 productos en el catálogo
                .verifyComplete();
    }

}
