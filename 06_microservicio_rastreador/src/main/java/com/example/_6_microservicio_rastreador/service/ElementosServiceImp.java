package com.example._6_microservicio_rastreador.service;

import com.example._6_microservicio_rastreador.model.Elemento;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class ElementosServiceImp implements ElementosService {
    String url1 = "http://localhost:8080/productos";
    String url2 = "http://localhost:8081/productos";

    @Override
    public Flux<Elemento> elementosPrecioMax(double precioMax) {
        Flux<Elemento> flux1 = catalogo(url1, "Tienda 1");
        Flux<Elemento> flux2 = catalogo(url2, "Tienda 2");
        return Flux.merge(flux1, flux2)
                .filter(elemento -> elemento.getPrecioProducto() <= precioMax);
    }

    private Flux<Elemento> catalogo(String url, String tienda) {
        WebClient webClient = WebClient.create(url);
        return webClient
                .get()
                .uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Elemento.class)
                .map(elemento -> {
                    elemento.setTienda(tienda);
                    return elemento;
                });
    }
}
