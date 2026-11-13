package com.example.employeemanagement.employee;

import com.example.employeemanagement.employee.dto.EmployeeRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Lab 6: form backing bean cho trang /employees/add.
// Dùng POJO getter/setter (không dùng record): th:field đọc qua getXxx() và Spring bind qua setXxx().
public class EmployeeForm {

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100, message = "Tên tối đa 100 ký tự")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 255, message = "Email tối đa 255 ký tự")
    private String email;

    @NotNull(message = "Phải chọn phòng ban")
    private Long departmentId;

    public EmployeeRequest toRequest() {
        return new EmployeeRequest(name, email, departmentId);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
}
