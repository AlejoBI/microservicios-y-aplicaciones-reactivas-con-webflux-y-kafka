package com.example.epg.service;

import com.example.epg.dto.SaldoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaldoCacheService {

    private static final Duration TTL = Duration.ofSeconds(30);

    private final ReactiveRedisTemplate<String, SaldoResponse> redisTemplate;

    public Mono<SaldoResponse> obtener(String cuenta) {
        return redisTemplate.opsForValue()
                .get(cuenta)
                .doOnNext(saldo -> log.info("Saldo obtenido de cache para cuenta {}", cuenta));
    }

    public Mono<Boolean> guardar(String cuenta, SaldoResponse saldo) {
        return redisTemplate.opsForValue()
                .set(cuenta, saldo, TTL)
                .doOnSuccess(r -> log.info("Saldo cacheado para cuenta {}", cuenta));
    }
}
