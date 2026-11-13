package com.example.employeemanagement.department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Lab 4: bảng department. Quan hệ chỉ khai báo một chiều ở phía Employee (@ManyToOne);
// không thêm @OneToMany List<Employee> vì chưa cần (≈ chỉ viết belongs_to, chưa viết has_many).
@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    protected Department() {}   // JPA bắt buộc có constructor không tham số

    public Department(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
