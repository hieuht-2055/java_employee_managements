package com.example.employeemanagement.report;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Lab 10: gom các con số thống kê. readOnly: cả 3 query chạy trong 1 transaction chỉ đọc.
@Service
@Transactional(readOnly = true)
public class EmployeeStatisticsService {

    private final EmployeeStatisticsRepository repository;

    public EmployeeStatisticsService(EmployeeStatisticsRepository repository) {
        this.repository = repository;
    }

    public EmployeeStatistics getStatistics() {
        long totalEmployees = repository.countEmployees();
        List<DepartmentStatistic> byDepartment = repository.countEmployeesByDepartment().stream()
            .map(row -> new DepartmentStatistic(row.departmentId(), row.departmentName(), row.employeeCount(),
                percentage(row.employeeCount(), totalEmployees)))
            .toList();
        return new EmployeeStatistics(totalEmployees, repository.countDepartments(), byDepartment);
    }

    // Làm tròn 1 chữ số thập phân; tổng = 0 thì trả 0 thay vì chia cho 0 (NaN)
    private static double percentage(long part, long total) {
        return total == 0 ? 0 : Math.round(part * 1000.0 / total) / 10.0;
    }
}
