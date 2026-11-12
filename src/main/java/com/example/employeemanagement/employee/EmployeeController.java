package com.example.employeemanagement.employee;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.employeemanagement.employee.dto.CreateEmployeeRequest;
import com.example.employeemanagement.employee.dto.EmployeeResponse;

// Lab 3: REST API nhân viên (≈ resources :employees, only: [:index, :show, :create]).
// Lưu ý: @RequestMapping ở class chỉ khai báo prefix URL; nếu dùng trên method mà không ghi method
// thì khớp MỌI HTTP method (không phải mặc định GET như slide nói).
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    // GET /api/employees              → tất cả
    // GET /api/employees?department=IT → @RequestParam (mặc định bắt buộc, nên phải required = false)
    @GetMapping
    public List<EmployeeResponse> list(@RequestParam(required = false) String department) {
        return service.list(department);
    }

    // GET /api/employees/1 → @PathVariable; không thấy thì 404 (Lab 5 sẽ chuyển sang exception + ProblemDetail)
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> get(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/employees + JSON body → 201 Created + header Location
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@RequestBody CreateEmployeeRequest req) {
        EmployeeResponse created = service.create(req);
        return ResponseEntity
            .created(URI.create("/api/employees/" + created.id()))
            .body(created);
    }
}
