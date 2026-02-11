package com.example._5_test_crud_productos.runners;

import com.example._5_test_crud_productos.model.Producto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TestRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        WebClient client = WebClient.create("http://localhost:8080/productos");
        /*Flux<Producto> flux = client
                .get()
                .uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Producto.class);
        flux.subscribe(System.out::println);*/

        /*client
                .post()
                .uri("/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new Producto(220, "prueba", "categoria test", 100.0, 10)), Producto.class)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnTerminate(() -> System.out.println("Producto creado con éxito"))
                .block();*/

        /*Mono<Producto> monoFind = client
                .get()
                .uri("/{cod}", 221)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Producto.class);
        monoFind.subscribe(producto -> System.out.println("Producto encontrado: " + producto));
        monoFind.switchIfEmpty(Mono.just(new Producto()).map(
                producto -> {
                    System.out.println("Producto no encontrado");
                    return producto;
                }
        )).block();*/

        client
                .delete()
                .uri("/{cod}", 220)
                .retrieve()
                .onStatus(h -> h.is4xxClientError(),
                        t -> {
                            System.out.println("Producto no encontrado para eliminar");
                            return Mono.empty();
                        })
                .bodyToMono(Producto.class)
                .subscribe(System.out::println);

    }
}
