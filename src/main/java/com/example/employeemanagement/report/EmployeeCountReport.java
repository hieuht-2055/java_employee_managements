package com.example.employeemanagement.report;

import java.time.Instant;

// generatedAt cho thấy kết quả lấy từ cache (giữ nguyên trong 60s) hay vừa tính lại.
public record EmployeeCountReport(long totalEmployees, Instant generatedAt) {}
