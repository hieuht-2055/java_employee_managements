package com.example.employeemanagement.department;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagement.common.DuplicateResourceException;
import com.example.employeemanagement.common.ResourceNotFoundException;

@Service
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository repository;

    public DepartmentService(DepartmentRepository repository) {
        this.repository = repository;
    }

    public List<DepartmentResponse> list() {
        return repository.findAllByOrderByNameAsc().stream().map(DepartmentResponse::from).toList();
    }

    @Transactional
    public DepartmentResponse create(CreateDepartmentRequest req) {
        String name = req.name().trim();
        if (repository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Phòng ban '" + name + "' đã tồn tại");
        }
        return DepartmentResponse.from(repository.save(new Department(name)));
    }

    // Dùng nội bộ cho EmployeeService: trả entity để gán vào Employee.department
    public Department getEntity(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department", id));
    }
}
