package com.example.epg.CAPAS.controller;

import com.example.epg.CAPAS.security.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public Mono<TokenResponse> login(@RequestBody LoginRequest request) {
        if ("admin".equals(request.getUser())
                && "admin".equals(request.getPassword())) {
            return Mono.just(new TokenResponse(jwtUtil.generateToken(request.getUser())));
        }
        return Mono.error(new RuntimeException("Credenciales inválidas"));
    }

    @Data
    static class LoginRequest {
        private String user;
        private String password;
    }

    @Data
    @AllArgsConstructor
    static class TokenResponse {
        private String token;
    }
}
