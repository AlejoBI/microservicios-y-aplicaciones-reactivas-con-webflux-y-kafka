package com.example.epg.config;

import com.example.epg.dto.SaldoResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, SaldoResponse> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory) {

        RedisSerializationContext<String, SaldoResponse> context = RedisSerializationContext
                .<String, SaldoResponse>newSerializationContext(new StringRedisSerializer())
                .value(new JacksonJsonRedisSerializer<>(SaldoResponse.class))
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
