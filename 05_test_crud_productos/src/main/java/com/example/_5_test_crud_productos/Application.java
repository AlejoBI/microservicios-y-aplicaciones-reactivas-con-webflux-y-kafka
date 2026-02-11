package com.example._5_test_crud_productos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example._5_test_crud_productos.runners")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
