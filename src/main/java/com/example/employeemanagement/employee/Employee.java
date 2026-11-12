package com.example.employeemanagement.employee;

// Lab 3: model nhân viên, lưu in-memory. Lab 4 sẽ thành @Entity (bảng employee, department_id → Department).
public class Employee {

    private Long id;
    private String code;
    private String name;
    private String email;
    private String department;

    public Employee(String name, String email, String department) {
        this.name = name;
        this.email = email;
        this.department = department;
    }

    // Repository gọi khi lưu lần đầu (≈ DB sinh id). Package-private: bên ngoài package không gọi được.
    void assignIdentity(Long id, String code) {
        this.id = id;
        this.code = code;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
}
