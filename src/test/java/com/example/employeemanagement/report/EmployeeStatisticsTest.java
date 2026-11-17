package com.example.employeemanagement.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagement.TestcontainersConfiguration;
import com.example.employeemanagement.department.Department;
import com.example.employeemanagement.department.DepartmentRepository;
import com.example.employeemanagement.employee.EmployeeService;
import com.example.employeemanagement.employee.dto.EmployeeRequest;

// Lab 10: thống kê bằng @Query (đếm theo phòng ban, LEFT JOIN giữ phòng ban 0 người, tỉ lệ %).
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@WithMockUser(roles = "USER")   // USER cũng được xem thống kê
@Transactional
class EmployeeStatisticsTest {

    @Autowired private MockMvc mvc;
    @Autowired private EmployeeStatisticsService statisticsService;
    @Autowired private EmployeeService employeeService;
    @Autowired private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        // DB test sạch (rollback sau mỗi test), chỉ có 3 phòng ban seed bởi Flyway V2: IT, HR, Sales
        Long rd = departmentRepository.save(new Department("Stat R&D")).getId();
        Long qa = departmentRepository.save(new Department("Stat QA")).getId();
        departmentRepository.save(new Department("Stat Empty"));
        employeeService.create(new EmployeeRequest("A", "a@stat.com", rd));
        employeeService.create(new EmployeeRequest("B", "b@stat.com", rd));
        employeeService.create(new EmployeeRequest("C", "c@stat.com", rd));
        employeeService.create(new EmployeeRequest("D", "d@stat.com", qa));
    }

    @Test
    void statistics_countsByDepartmentIncludingEmptyOnes() {
        EmployeeStatistics stats = statisticsService.getStatistics();

        assertThat(stats.totalEmployees()).isEqualTo(4);
        assertThat(stats.totalDepartments()).isEqualTo(6);
        // Sắp xếp giảm dần theo số nhân viên; phòng ban 0 người vẫn có mặt nhờ LEFT JOIN
        assertThat(stats.byDepartment().getFirst())
            .isEqualTo(new DepartmentStatistic(stats.byDepartment().getFirst().departmentId(), "Stat R&D", 3, 75.0));
        assertThat(stats.byDepartment())
            .anySatisfy(d -> {
                assertThat(d.departmentName()).isEqualTo("Stat QA");
                assertThat(d.employeeCount()).isEqualTo(1);
                assertThat(d.percentage()).isEqualTo(25.0);
            })
            .anySatisfy(d -> {
                assertThat(d.departmentName()).isEqualTo("Stat Empty");
                assertThat(d.employeeCount()).isZero();
                assertThat(d.percentage()).isZero();
            })
            .hasSize(6);
    }

    @Test
    void statisticsApi_returnsJson() throws Exception {
        mvc.perform(get("/api/reports/statistics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalEmployees").value(4))
            .andExpect(jsonPath("$.byDepartment[0].departmentName").value("Stat R&D"))
            .andExpect(jsonPath("$.byDepartment[0].percentage").value(75.0));
    }

    @Test
    void statisticsPage_rendersTable() throws Exception {
        mvc.perform(get("/employees/statistics"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Stat R&amp;D")))
            .andExpect(content().string(containsString("75.0%")))
            .andExpect(content().string(containsString("width:75.0%")));
    }
}
