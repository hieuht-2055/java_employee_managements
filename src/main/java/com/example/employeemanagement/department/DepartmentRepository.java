package com.example.employeemanagement.department;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data tự sinh implementation lúc khởi động (parse tên method → JPQL).
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);
}
