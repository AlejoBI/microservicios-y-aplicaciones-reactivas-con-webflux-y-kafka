package com.example.epg.HEXAGONAL.infrastructure.config;

import com.example.epg.HEXAGONAL.domain.model.Saldo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * CONFIGURACIÓN DE REDIS
 *
 * En arquitectura hexagonal:
 * - Esta clase está en INFRASTRUCTURE/config
 * - Configura detalles técnicos (serialización, conexión)
 * - NO es visible para el dominio
 *
 * Configura:
 * - ReactiveRedisTemplate para el modelo de dominio (Saldo)
 * - Serialización JSON con Jackson
 * - String como clave, Saldo como valor
 */
@Configuration
public class RedisConfig {

    /**
     * Template de Redis para trabajar con el modelo de dominio Saldo
     *
     * @param factory Factoría de conexiones reactivas a Redis
     * @return ReactiveRedisTemplate configurado
     */
    @Bean
    public ReactiveRedisTemplate<String, Saldo> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory) {

        // Serializador para claves (String)
        StringRedisSerializer keySerializer = new StringRedisSerializer();

        // Serializador para valores (Saldo en JSON)
        Jackson2JsonRedisSerializer<Saldo> valueSerializer =
                new Jackson2JsonRedisSerializer<>(Saldo.class);

        // Contexto de serialización
        RedisSerializationContext<String, Saldo> serializationContext =
                RedisSerializationContext
                        .<String, Saldo>newSerializationContext(keySerializer)
                        .value(valueSerializer)
                        .hashKey(keySerializer)
                        .hashValue(valueSerializer)
                        .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }
}
