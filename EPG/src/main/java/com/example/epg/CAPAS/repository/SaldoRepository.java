package com.example.epg.CAPAS.repository;

import com.example.epg.CAPAS.domain.SaldoEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SaldoRepository extends ReactiveCrudRepository<SaldoEntity, Long> {
    Mono<SaldoEntity> findByNumeroCuenta(String numeroCuenta);
}
