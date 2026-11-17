package com.example.employeemanagement.report;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Lab 10: trang thống kê /employees/statistics (USER và ADMIN đều xem được).
@Controller
public class StatisticsPageController {

    private final EmployeeStatisticsService statisticsService;

    public StatisticsPageController(EmployeeStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/employees/statistics")
    String statistics(Model model) {
        model.addAttribute("stats", statisticsService.getStatistics());
        return "employees/statistics";
    }
}
