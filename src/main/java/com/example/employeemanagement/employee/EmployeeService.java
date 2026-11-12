package com.example.employeemanagement.employee;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.employeemanagement.common.UtilityService;
import com.example.employeemanagement.employee.dto.CreateEmployeeRequest;
import com.example.employeemanagement.employee.dto.EmployeeResponse;

// Lab 2 + 3: tầng nghiệp vụ. Nhận 2 bean qua constructor injection.
@Service
public class EmployeeService {

    private final InMemoryEmployeeRepository repository;
    private final UtilityService utilityService;

    public EmployeeService(InMemoryEmployeeRepository repository, UtilityService utilityService) {
        this.repository = repository;
        this.utilityService = utilityService;
    }

    // department == null → lấy tất cả; có giá trị → lọc, không phân biệt hoa thường.
    public List<EmployeeResponse> list(String department) {
        return repository.findAll().stream()
            .filter(e -> department == null || department.equalsIgnoreCase(e.getDepartment()))
            .map(EmployeeResponse::from)
            .toList();
    }

    public Optional<EmployeeResponse> findById(Long id) {
        return repository.findById(id).map(EmployeeResponse::from);
    }

    public EmployeeResponse create(CreateEmployeeRequest req) {
        Employee employee = new Employee(
            utilityService.formatName(req.name()),
            utilityService.normalizeEmail(req.email()),
            req.department());
        return EmployeeResponse.from(repository.save(employee));
    }
}
