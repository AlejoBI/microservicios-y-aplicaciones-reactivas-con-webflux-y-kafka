package com.example.epg.CAPAS.security;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public Mono<Authentication> authenticate(String token) {
        try {
            String user = jwtUtil.validarYObtenerUsuario(token);
            return Mono.just(
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null, // No necesitamos credenciales aquí porque ya estamos autenticados
                            List.of(new SimpleGrantedAuthority("ROLE_USER")))); // Asignar rol USER
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Token inválido o expirado"));
        }
    }
}
