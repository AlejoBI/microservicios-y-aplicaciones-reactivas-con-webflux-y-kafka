package com.example.epg.controller;

import com.example.epg.exception.SaldoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SaldoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<String> manejarSaldoNoEncontradoException(SaldoNoEncontradoException ex) {
        return Mono.just(ex.getMessage());
    }
}
