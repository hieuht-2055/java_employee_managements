package com.example.employeemanagement.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "Username không được để trống") String username,
    @NotBlank(message = "Mật khẩu không được để trống") String password) {}
