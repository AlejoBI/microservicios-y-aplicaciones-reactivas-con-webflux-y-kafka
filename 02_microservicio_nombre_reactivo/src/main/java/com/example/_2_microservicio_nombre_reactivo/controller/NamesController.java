package com.example._2_microservicio_nombre_reactivo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@RestController
public class NamesController {

    @GetMapping("/names")
    public Flux<String> getNames() {
        List<String> names = List.of("Alice", "Bob", "Charlie", "Diana", "Eve");
        return Flux.fromIterable(names)
                .delayElements(Duration.ofSeconds(2)); // Simula un retraso en la emisión de cada nombre
    }
}
