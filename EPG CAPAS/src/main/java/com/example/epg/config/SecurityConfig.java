package com.example.epg.config;

import com.example.epg.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter authbFilter = new AuthenticationWebFilter(authenticationManager());

        authbFilter.setServerAuthenticationConverter(exchange -> {
            String header = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                return jwtAuthenticationFilter.authenticate(token);
            }
            return Mono.empty();
        });

        return http
                // Deshabilitamos CSRF porque no es necesario para APIs REST
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // No mantenemos sesión, cada petición debe ser autenticada
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                // Configuramos las reglas de autorización para las rutas
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**").permitAll()
                        // Cualquier otra ruta requiere autenticación
                        .anyExchange().authenticated())
                // Agregamos nuestro filtro de autenticación personalizado en el orden correcto
                .addFilterAt(authbFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return authentication -> Mono.just(authentication);
    }
}
