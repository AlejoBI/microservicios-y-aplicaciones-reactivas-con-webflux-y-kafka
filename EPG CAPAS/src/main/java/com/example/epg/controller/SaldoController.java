package com.example.epg.controller;

import com.example.epg.dto.SaldoResponse;
import com.example.epg.service.SaldoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/saldos")
@RequiredArgsConstructor
public class SaldoController {

    private final SaldoService saldoService;

    @GetMapping("/{cuenta}")
    public Mono<SaldoResponse> obtenerSaldo(@PathVariable String cuenta) {
        return saldoService.consultarSaldo(cuenta);
    }
}
