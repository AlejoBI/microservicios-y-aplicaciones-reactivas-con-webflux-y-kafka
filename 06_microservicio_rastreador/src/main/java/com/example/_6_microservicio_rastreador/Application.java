package com.example._6_microservicio_rastreador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example._6_microservicio_rastreador.controller", "com.example._6_microservicio_rastreador.service"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
