package com.example.epg.repository;

import com.example.epg.domain.SaldoEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SaldoRepository extends ReactiveCrudRepository<SaldoEntity, Long> {
    Mono<SaldoEntity> findByNumeroCuenta(String numeroCuenta);
}
