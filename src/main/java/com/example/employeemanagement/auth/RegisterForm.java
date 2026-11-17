package com.example.employeemanagement.auth;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Lab 9: form backing bean cho trang /register (POJO getter/setter để th:field bind được).
public class RegisterForm {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username dài 3–50 ký tự")
    @Pattern(regexp = "[a-zA-Z0-9._-]*", message = "Username chỉ gồm chữ, số và . _ -")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 72, message = "Mật khẩu dài 8–72 ký tự")
    private String password;

    private String confirmPassword;

    // Ràng buộc giữa 2 field: Bean Validation gọi isXxx() và báo lỗi trên field ảo "passwordConfirmed"
    @AssertTrue(message = "Mật khẩu nhập lại không khớp")
    public boolean isPasswordConfirmed() {
        return password == null || password.equals(confirmPassword);
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
