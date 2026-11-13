package com.example.employeemanagement.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Body của POST /api/employees và PUT /api/employees/{id} (≈ strong params + validates trong model).
// Validation chỉ chạy khi tham số controller có @Valid.
public record EmployeeRequest(
    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100, message = "Tên tối đa 100 ký tự")
    String name,

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 255, message = "Email tối đa 255 ký tự")
    String email,

    @NotNull(message = "Phải chọn phòng ban")
    Long departmentId) {}
