package com.example.employeemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Lab 2: bean định nghĩa bằng @Bean trong @Configuration.
// Dùng cách này khi class thuộc thư viện bên ngoài (không gắn @Component vào được)
// hoặc cần tự cấu hình khi khởi tạo (ở đây: strength = 10).
// @Configuration (proxyBeanMethods = true) bảo đảm gọi passwordEncoder() nhiều lần vẫn ra cùng một singleton.
@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
