package com.example.employeemanagement.employee;

import com.example.employeemanagement.department.Department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Lab 4: bảng employee (id, code, name, email, department_id).
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // ≈ belongs_to :department. ToOne mặc định EAGER → đổi sang LAZY, cần thì JOIN FETCH trong query.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    protected Employee() {}

    public Employee(String name, String email, Department department) {
        this.name = name;
        this.email = email;
        this.department = department;
    }

    public void update(String name, String email, Department department) {
        this.name = name;
        this.email = email;
        this.department = department;
    }

    // Mã nhân viên cần id nên gán sau khi INSERT; dirty checking tự sinh UPDATE lúc commit.
    void assignCode(String code) {
        this.code = code;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Department getDepartment() { return department; }
}
