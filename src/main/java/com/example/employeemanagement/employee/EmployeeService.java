package com.example.employeemanagement.employee;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagement.common.DuplicateResourceException;
import com.example.employeemanagement.common.ResourceNotFoundException;
import com.example.employeemanagement.common.UtilityService;
import com.example.employeemanagement.department.Department;
import com.example.employeemanagement.department.DepartmentService;
import com.example.employeemanagement.employee.dto.EmployeeRequest;
import com.example.employeemanagement.employee.dto.EmployeeResponse;

// Tầng nghiệp vụ dùng chung cho REST API (Lab 4, 5) và trang Thymeleaf (Lab 6).
// Mặc định readOnly; method ghi tự khai báo @Transactional (ghi đè cấu hình ở class).
@Service
@Transactional(readOnly = true)
public class EmployeeService {

    // Lab 7: SLF4J là API (≈ Rails.logger), Logback là implementation Boot dùng mặc định.
    // Dùng placeholder {} thay vì nối chuỗi: chuỗi chỉ được ghép khi level đó thực sự được bật.
    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository repository;
    private final DepartmentService departmentService;
    private final UtilityService utilityService;

    public EmployeeService(EmployeeRepository repository, DepartmentService departmentService,
                           UtilityService utilityService) {
        this.repository = repository;
        this.departmentService = departmentService;
        this.utilityService = utilityService;
    }

    // name / departmentId null hoặc rỗng = không lọc theo điều kiện đó
    public List<EmployeeResponse> search(String name, Long departmentId) {
        String keyword = (name == null || name.isBlank()) ? null : name.trim();
        log.debug("Search employees name={} departmentId={}", keyword, departmentId);
        return repository.search(keyword, departmentId).stream().map(EmployeeResponse::from).toList();
    }

    public EmployeeResponse get(Long id) {
        return EmployeeResponse.from(findWithDepartment(id));
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest req) {
        String email = utilityService.normalizeEmail(req.email());
        if (repository.existsByEmail(email)) {
            log.warn("Create employee rejected: duplicate email");
            throw duplicateEmail(email);
        }
        Department department = departmentService.getEntity(req.departmentId());
        Employee employee = repository.save(
            new Employee(utilityService.formatName(req.name()), email, department));
        employee.assignCode(utilityService.generateEmployeeCode(employee.getId()));
        // Chỉ log id/code/phòng ban, không log email/tên (dữ liệu cá nhân)
        log.info("Created employee id={} code={} departmentId={}",
            employee.getId(), employee.getCode(), department.getId());
        return EmployeeResponse.from(employee);
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest req) {
        Employee employee = findWithDepartment(id);
        String email = utilityService.normalizeEmail(req.email());
        if (repository.existsByEmailAndIdNot(email, id)) {
            log.warn("Update employee id={} rejected: duplicate email", id);
            throw duplicateEmail(email);
        }
        Department department = departmentService.getEntity(req.departmentId());
        // Không cần gọi save(): entity đang được quản lý, dirty checking tự UPDATE khi commit
        employee.update(utilityService.formatName(req.name()), email, department);
        log.info("Updated employee id={} code={} departmentId={}", id, employee.getCode(), department.getId());
        return EmployeeResponse.from(employee);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Delete employee id={} rejected: not found", id);
            throw new ResourceNotFoundException("Employee", id);
        }
        repository.deleteById(id);
        log.info("Deleted employee id={}", id);
    }

    private Employee findWithDepartment(Long id) {
        return repository.findWithDepartmentById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }

    private static DuplicateResourceException duplicateEmail(String email) {
        return new DuplicateResourceException("Email '" + email + "' đã được sử dụng");
    }
}
