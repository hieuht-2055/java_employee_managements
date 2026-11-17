package com.example.employeemanagement.report;

// Một dòng thống kê theo phòng ban, đã tính tỉ lệ % trên tổng số nhân viên.
public record DepartmentStatistic(Long departmentId, String departmentName, long employeeCount, double percentage) {}
