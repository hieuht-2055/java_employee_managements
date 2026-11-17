package com.example.employeemanagement.auth.jwt;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

// Lab 9: app vừa PHÁT HÀNH token (JwtEncoder, ở /api/auth/login) vừa KIỂM TRA token (JwtDecoder,
// do BearerTokenAuthenticationFilter của oauth2-resource-server gọi). Hai bên dùng chung khoá HS256.
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    private final SecretKey secretKey;

    public JwtConfig(JwtProperties properties) {
        this.secretKey = new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        // Security 7.1: builder mới, không cần tự dựng JWKSource/ImmutableSecret như hướng dẫn cũ
        return NimbusJwtEncoder.withSecretKey(secretKey).algorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Mặc định kiểm tra chữ ký + exp/nbf (lệch giờ cho phép 60s)
        return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
    }
}
