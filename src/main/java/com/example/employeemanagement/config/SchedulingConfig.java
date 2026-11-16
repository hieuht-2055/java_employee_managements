package com.example.employeemanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// Lab 8: bật @Scheduled (≈ sidekiq-cron / whenever, nhưng chạy ngay trong process của app).
// Thiếu @EnableScheduling thì method @Scheduled không bao giờ chạy, cũng không báo lỗi.
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
