package com.example.employeemanagement.auth;

// Lưu vào DB dạng chuỗi "USER" / "ADMIN" (không có tiền tố ROLE_).
// Spring Security tự thêm ROLE_ khi dùng .roles(...) và hasRole(...).
public enum Role {
    USER,
    ADMIN
}
