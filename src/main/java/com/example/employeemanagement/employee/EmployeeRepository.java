package com.example.employeemanagement.employee;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Lab 4: thay InMemoryEmployeeRepository. Spring Data tự sinh implementation (SimpleJpaRepository).
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Tìm theo tên (chứa, không phân biệt hoa thường) và/hoặc phòng ban; tham số null = bỏ qua điều kiện đó.
    // JOIN FETCH nạp luôn department trong 1 câu SQL (≈ includes(:department)) → tránh N+1.
    // cast(:name as string): khi name = null, Hibernate gửi null không kèm kiểu và Postgres đoán là bytea
    // → lỗi "function lower(bytea) does not exist". Ép kiểu rõ ràng để tránh.
    @Query("""
        select e from Employee e join fetch e.department d
        where (:name is null or lower(e.name) like lower(concat('%', cast(:name as string), '%')))
          and (:departmentId is null or d.id = :departmentId)
        order by e.id
        """)
    List<Employee> search(@Param("name") String name, @Param("departmentId") Long departmentId);

    // ≈ Employee.includes(:department).find_by(id:)
    @EntityGraph(attributePaths = "department")
    Optional<Employee> findWithDepartmentById(Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
