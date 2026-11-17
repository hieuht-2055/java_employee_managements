package com.example.employeemanagement.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Đăng ký qua API luôn ra role USER; không cho client tự chọn role (tránh tự phong ADMIN).
public record RegisterRequest(
    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username dài 3–50 ký tự")
    @Pattern(regexp = "[a-zA-Z0-9._-]*", message = "Username chỉ gồm chữ, số và . _ -")
    String username,

    // BCrypt chỉ dùng 72 byte đầu; Security 7 ném lỗi nếu dài hơn → chặn từ validation
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 72, message = "Mật khẩu dài 8–72 ký tự")
    String password) {}
