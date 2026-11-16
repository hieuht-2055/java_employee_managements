package com.example.employeemanagement.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

// Lab 8: bật @Cacheable/@CacheEvict. Thiếu @EnableCaching thì các annotation cache bị bỏ qua im lặng.
// Cache manager (Caffeine, TTL 60s) do Boot tự tạo từ spring.cache.* trong application.yml.
@Configuration
@EnableCaching
public class CacheConfig {
}
