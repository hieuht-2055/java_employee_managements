package com.example.employeemanagement.report;

// Kết quả thô của query "đếm nhân viên theo phòng ban" (JPQL constructor expression: select new ...).
public record DepartmentEmployeeCount(Long departmentId, String departmentName, long employeeCount) {}
