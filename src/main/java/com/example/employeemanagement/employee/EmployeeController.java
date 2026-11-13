package com.example.employeemanagement.employee;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.employeemanagement.employee.dto.EmployeeRequest;
import com.example.employeemanagement.employee.dto.EmployeeResponse;

import jakarta.validation.Valid;

// REST API nhân viên (≈ resources :employees, except: [:new, :edit]).
// Lỗi (validation, 404, 409, JSON hỏng) do GlobalApiExceptionHandler xử lý, controller không try/catch.
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    // GET /api/employees                         → tất cả
    // GET /api/employees?name=an&departmentId=1  → tìm theo tên và/hoặc phòng ban
    @GetMapping
    public List<EmployeeResponse> list(@RequestParam(required = false) String name,
                                       @RequestParam(required = false) Long departmentId) {
        return service.search(name, departmentId);
    }

    // GET /api/employees/1 → 404 ProblemDetail nếu không có
    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    // POST /api/employees → 201 + Location; dữ liệu sai → 400 kèm lỗi từng field
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest req) {
        EmployeeResponse created = service.create(req);
        return ResponseEntity.created(URI.create("/api/employees/" + created.id())).body(created);
    }

    // PUT /api/employees/1 → thay toàn bộ thông tin
    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest req) {
        return service.update(id, req);
    }

    // DELETE /api/employees/1 → 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
