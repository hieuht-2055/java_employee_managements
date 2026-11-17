package com.example.employeemanagement.report;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Lab 8: GET /api/reports/employee-count → {"totalEmployees": 5, "generatedAt": "..."} (cache 60s)
// Lab 10: GET /api/reports/statistics     → tổng số + số nhân viên theo phòng ban (không cache)
@RestController
@RequestMapping("/api/reports")
public class EmployeeReportController {

    private final EmployeeReportService reportService;
    private final EmployeeStatisticsService statisticsService;

    public EmployeeReportController(EmployeeReportService reportService,
                                    EmployeeStatisticsService statisticsService) {
        this.reportService = reportService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/employee-count")
    public EmployeeCountReport employeeCount() {
        return reportService.countEmployees();
    }

    @GetMapping("/statistics")
    public EmployeeStatistics statistics() {
        return statisticsService.getStatistics();
    }
}
