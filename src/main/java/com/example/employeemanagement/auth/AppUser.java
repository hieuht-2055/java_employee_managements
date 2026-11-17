package com.example.employeemanagement.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Lab 9: User entity (username, password, role). ≈ model User của Devise.
@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;   // BCrypt hash

    // EnumType.STRING: lưu "ADMIN". Mặc định ORDINAL lưu số thứ tự → đổi thứ tự enum là hỏng dữ liệu.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    protected AppUser() {}

    public AppUser(String username, String encodedPassword, Role role) {
        this.username = username;
        this.password = encodedPassword;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
}
