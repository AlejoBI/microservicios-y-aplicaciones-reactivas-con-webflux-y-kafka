package com.example.epg.exception;

public class SaldoNoEncontradoException extends RuntimeException {

    public SaldoNoEncontradoException(String cuenta) {
        super("No se encontró saldo para la cuenta: " + cuenta);
    }
}
