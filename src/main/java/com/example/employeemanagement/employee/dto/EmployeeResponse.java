package com.example.employeemanagement.employee.dto;

import com.example.employeemanagement.employee.Employee;

// JSON trả về cho client và dữ liệu cho trang Thymeleaf (≈ serializer). Không lộ entity ra ngoài.
// Gọi from() khi department đã được nạp (JOIN FETCH / @EntityGraph) hoặc còn trong transaction.
public record EmployeeResponse(
    Long id, String code, String name, String email, Long departmentId, String departmentName) {

    public static EmployeeResponse from(Employee e) {
        return new EmployeeResponse(e.getId(), e.getCode(), e.getName(), e.getEmail(),
            e.getDepartment().getId(), e.getDepartment().getName());
    }
}
