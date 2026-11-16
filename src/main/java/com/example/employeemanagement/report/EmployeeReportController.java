package com.example.employeemanagement.report;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Lab 8: GET /api/reports/employee-count → {"totalEmployees": 5, "generatedAt": "..."}
// Lab 10 sẽ thêm các báo cáo thống kê khác vào đây.
@RestController
@RequestMapping("/api/reports")
public class EmployeeReportController {

    private final EmployeeReportService reportService;

    public EmployeeReportController(EmployeeReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/employee-count")
    public EmployeeCountReport employeeCount() {
        return reportService.countEmployees();
    }
}
