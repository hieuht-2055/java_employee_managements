package com.example.employeemanagement.report;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagement.employee.EmployeeRepository;

// Lab 8: báo cáo tổng số nhân viên, cache 60 giây (≈ Rails.cache.fetch("employee_count", expires_in: 1.minute)).
// @Cacheable chạy qua proxy giống @Transactional → gọi từ BEAN KHÁC mới có tác dụng
// (gọi this.countEmployees() trong cùng class là self-invocation, bỏ qua cache).
// Cố ý không @CacheEvict khi thêm/xoá nhân viên: báo cáo chấp nhận trễ tối đa 1 phút.
@Service
@Transactional(readOnly = true)
public class EmployeeReportService {

    public static final String EMPLOYEE_COUNT_CACHE = "employeeCount";

    private static final Logger log = LoggerFactory.getLogger(EmployeeReportService.class);

    private final EmployeeRepository employeeRepository;

    public EmployeeReportService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Không tham số → key mặc định SimpleKey.EMPTY, cả app dùng chung 1 entry
    @Cacheable(EMPLOYEE_COUNT_CACHE)
    public EmployeeCountReport countEmployees() {
        log.debug("Cache miss: counting employees from database");
        return new EmployeeCountReport(employeeRepository.count(), Instant.now());
    }
}
