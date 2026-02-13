package com.example.epg.HEXAGONAL.infrastructure.adapter.in.rest;

import com.example.epg.HEXAGONAL.domain.exception.SaldoNoEncontradoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * MANEJADOR GLOBAL DE EXCEPCIONES
 *
 * En arquitectura hexagonal:
 * - Pertenece a INFRASTRUCTURE/adapter/in/rest
 * - Convierte excepciones del dominio a respuestas HTTP
 * - Traduce Dominio → HTTP
 *
 * SOLID aplicado:
 * - Single Responsibility: Solo maneja la traducción de errores
 * - Open/Closed: Podemos agregar más handlers sin modificar existentes
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja la excepción de dominio SaldoNoEncontradoException
     *
     * @param ex Excepción de dominio
     * @return 404 NOT FOUND con detalles del error
     */
    @ExceptionHandler(SaldoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleSaldoNoEncontrado(
            SaldoNoEncontradoException ex) {

        log.warn("⚠️ Saldo no encontrado: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Saldo no encontrado",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja excepciones genéricas no capturadas
     *
     * @param ex Excepción genérica
     * @return 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("❌ Error interno del servidor: ", ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor",
                "Ha ocurrido un error inesperado",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * DTO para respuestas de error
     */
    public record ErrorResponse(
            int status,
            String error,
            String message,
            LocalDateTime timestamp
    ) {}
}
