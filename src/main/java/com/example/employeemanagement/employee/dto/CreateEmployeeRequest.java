package com.example.employeemanagement.employee.dto;

// Body của POST /api/employees (≈ strong params). Lab 5 sẽ thêm @NotBlank, @Email + @Valid.
public record CreateEmployeeRequest(String name, String email, String department) {}
