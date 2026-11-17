package com.example.employeemanagement.auth.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Lab 9: gom cấu hình app.jwt.* vào một record có kiểu (≈ Rails credentials + config_for).
// Kiểm tra ngay lúc khởi động: thiếu hoặc yếu secret thì app dừng, không chạy với khoá rỗng.
@ConfigurationProperties("app.jwt")
public record JwtProperties(String secret, Duration ttl, String issuer) {

    public JwtProperties {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                "app.jwt.secret phải có ít nhất 32 byte (HS256 cần khoá 256 bit). Đặt biến môi trường JWT_SECRET.");
        }
        if (ttl == null) {
            ttl = Duration.ofHours(1);
        }
        if (issuer == null || issuer.isBlank()) {
            issuer = "employee-management";
        }
    }
}
