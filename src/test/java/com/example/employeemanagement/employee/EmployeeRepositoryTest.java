package com.example.employeemanagement.employee;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import com.example.employeemanagement.TestcontainersConfiguration;
import com.example.employeemanagement.department.Department;
import com.example.employeemanagement.department.DepartmentRepository;

// Lab 4: test tầng repository. @DataJpaTest chỉ nạp JPA + Flyway (không nạp controller/service),
// mỗi test chạy trong transaction và rollback khi xong (≈ use_transactional_tests của Rails).
// replace = NONE: dùng Postgres của Testcontainers thay vì DB nhúng mặc định.
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department it;
    private Department hr;

    @BeforeEach
    void setUp() {
        it = departmentRepository.save(new Department("Test IT"));
        hr = departmentRepository.save(new Department("Test HR"));
        employeeRepository.save(new Employee("Nguyễn Văn An", "an@test.com", it));
        employeeRepository.save(new Employee("Trần Thị Anh", "anh@test.com", hr));
        employeeRepository.save(new Employee("Lê Bình", "binh@test.com", it));
    }

    private static List<String> names(List<Employee> employees) {
        return employees.stream().map(Employee::getName).toList();
    }

    @Test
    void search_withoutFilters_returnsAllOrderedById() {
        assertThat(names(employeeRepository.search(null, null)))
            .containsExactly("Nguyễn Văn An", "Trần Thị Anh", "Lê Bình");
    }

    @Test
    void search_byNameIsCaseInsensitiveContains() {
        assertThat(names(employeeRepository.search("AN", null)))
            .containsExactly("Nguyễn Văn An", "Trần Thị Anh");
    }

    @Test
    void search_byDepartment() {
        assertThat(names(employeeRepository.search(null, it.getId())))
            .containsExactly("Nguyễn Văn An", "Lê Bình");
    }

    @Test
    void search_byNameAndDepartment() {
        assertThat(names(employeeRepository.search("an", hr.getId())))
            .containsExactly("Trần Thị Anh");
    }

    @Test
    void migrationSeedsDefaultDepartments() {
        assertThat(departmentRepository.existsByNameIgnoreCase("it")).isTrue();
        assertThat(departmentRepository.existsByNameIgnoreCase("Sales")).isTrue();
    }
}
