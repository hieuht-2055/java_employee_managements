package com.example.employeemanagement.department;

public record DepartmentResponse(Long id, String name) {

    public static DepartmentResponse from(Department d) {
        return new DepartmentResponse(d.getId(), d.getName());
    }
}
