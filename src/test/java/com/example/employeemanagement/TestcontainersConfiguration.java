package com.example.employeemanagement;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

// Test chạy trên Postgres 17 thật trong Docker (giống môi trường dev), không dùng H2.
// @ServiceConnection: Boot tự lấy url/username/password của container thay cho spring.datasource.*
// Các test class cùng @Import cấu hình này dùng chung Spring context (cache) → chỉ khởi động 1 container.
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer("postgres:17");
    }
}
