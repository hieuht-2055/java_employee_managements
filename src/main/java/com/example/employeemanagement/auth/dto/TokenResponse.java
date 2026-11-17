package com.example.employeemanagement.auth.dto;

import java.util.List;

// Kết quả POST /api/auth/login. Client gửi lại: Authorization: Bearer <accessToken>
public record TokenResponse(String accessToken, String tokenType, long expiresIn, String username, List<String> roles) {}
