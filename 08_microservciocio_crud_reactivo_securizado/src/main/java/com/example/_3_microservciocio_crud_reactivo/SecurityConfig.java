package com.example._3_microservciocio_crud_reactivo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.List;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public MapReactiveUserDetailsService users() throws Exception {
        List<UserDetails> users = List.of(
                User.withUsername("user1")
                        .password("{noop}password1")
                        .roles("USER")
                        .build(),
                User.withUsername("admin")
                        .password("{noop}adminpass")
                        .roles("USER", "ADMIN")
                        .build(),
                User.withUsername("user2")
                        .password("{noop}password2")
                        .roles("OPERATOR")
                        .build()
        );
        return new MapReactiveUserDetailsService(users);
    }

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {
        return http
                // Deshabilitamos CSRF porque no es necesario para APIs REST
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // Configuramos la autorización de las rutas
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/productos/").hasAnyRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/productos/**").hasAnyRole("ADMIN", "OPERATOR")
                        .pathMatchers(HttpMethod.PUT, "/productos/**").hasAnyRole("ADMIN", "OPERATOR")
                        .pathMatchers(HttpMethod.GET, "/productos/**").permitAll()
                        .pathMatchers("/productos/**").authenticated()
                        .anyExchange().permitAll())
                // Configuramos la autenticación HTTP Basic
                .httpBasic(Customizer.withDefaults())
                .build();
    }

}
