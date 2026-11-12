package com.example.employeemanagement.employee.dto;

import com.example.employeemanagement.employee.Employee;

// JSON trả về cho client (≈ serializer). Không trả thẳng model ra ngoài.
public record EmployeeResponse(Long id, String code, String name, String email, String department) {

    public static EmployeeResponse from(Employee e) {
        return new EmployeeResponse(e.getId(), e.getCode(), e.getName(), e.getEmail(), e.getDepartment());
    }
}
