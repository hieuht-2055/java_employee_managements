package com.example.employeemanagement.auth.dto;

import com.example.employeemanagement.auth.AppUser;

// Không bao giờ trả password (kể cả hash) ra ngoài.
public record UserResponse(Long id, String username, String role) {

    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name());
    }
}
