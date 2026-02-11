package com.example._2_microservicio_nombre_reactivo;

import com.example._2_microservicio_nombre_reactivo.controller.NamesController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
class ApplicationTests {

    @Autowired
    NamesController namesController;

    @Test
    void contextLoads() {
        StepVerifier.create(namesController.getNames())
                .expectNext("Alice")
                .expectNext("Bob", "Charlie", "Diana") // Espera los siguientes 3 nombres
                .expectNextCount(1) // Espera un nombre más después de los primeros 4 falta Eve
                .verifyComplete();
    }

}
