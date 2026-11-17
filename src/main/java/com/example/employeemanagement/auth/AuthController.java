package com.example.employeemanagement.auth;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.employeemanagement.auth.dto.LoginRequest;
import com.example.employeemanagement.auth.dto.RegisterRequest;
import com.example.employeemanagement.auth.dto.TokenResponse;
import com.example.employeemanagement.auth.dto.UserResponse;

import jakarta.validation.Valid;

// Lab 9: API xác thực.
// POST /api/auth/register  {"username","password"}  → 201, role USER
// POST /api/auth/login     {"username","password"}  → {"accessToken": "...", "tokenType": "Bearer", ...}
// GET  /api/auth/me        (Authorization: Bearer ...) → thông tin lấy từ token
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req.username(), req.password());
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    // Spring tự inject Authentication hiện tại (≈ current_user). Với JWT là JwtAuthenticationToken.
    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority).toList();
        return Map.of("username", authentication.getName(), "authorities", authorities);
    }
}
