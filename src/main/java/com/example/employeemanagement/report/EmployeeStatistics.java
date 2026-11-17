package com.example.employeemanagement.report;

import java.util.List;

// Lab 10: kết quả GET /api/reports/statistics và trang /employees/statistics.
public record EmployeeStatistics(long totalEmployees, long totalDepartments, List<DepartmentStatistic> byDepartment) {}
