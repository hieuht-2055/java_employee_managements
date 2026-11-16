package com.example.employeemanagement.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagement.TestcontainersConfiguration;
import com.example.employeemanagement.department.Department;
import com.example.employeemanagement.department.DepartmentRepository;
import com.example.employeemanagement.employee.EmployeeService;
import com.example.employeemanagement.employee.dto.EmployeeRequest;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

// Lab 8: cache báo cáo, Actuator, scheduled task.
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class ReportCacheAndActuatorTest {

    @Autowired private MockMvc mvc;
    @Autowired private CacheManager cacheManager;
    @Autowired private EmployeeService employeeService;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private ScheduledTaskHolder scheduledTaskHolder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void clearCache() {
        // Cache là bean singleton, không rollback theo transaction → xoá trước mỗi test
        Objects.requireNonNull(cacheManager.getCache(EmployeeReportService.EMPLOYEE_COUNT_CACHE)).clear();
    }

    private JsonNode fetchEmployeeCount() throws Exception {
        String json = mvc.perform(get("/api/reports/employee-count"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(json);
    }

    @Test
    void employeeCount_isCachedUntilEvicted() throws Exception {
        JsonNode first = fetchEmployeeCount();

        Long deptId = departmentRepository.save(new Department("Report IT")).getId();
        employeeService.create(new EmployeeRequest("Cache Test", "cache@example.com", deptId));

        // Trong 60s: vẫn trả kết quả cũ từ cache (cùng generatedAt, chưa thấy nhân viên mới)
        JsonNode cached = fetchEmployeeCount();
        assertThat(cached).isEqualTo(first);

        // Hết hạn (ở đây giả lập bằng clear) → tính lại từ DB
        clearCache();
        JsonNode fresh = fetchEmployeeCount();
        assertThat(fresh.get("totalEmployees").asLong()).isEqualTo(first.get("totalEmployees").asLong() + 1);
        assertThat(fresh.get("generatedAt")).isNotEqualTo(first.get("generatedAt"));
    }

    @Test
    void actuator_exposesHealthInfoMetricsAndCaches() throws Exception {
        mvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
        mvc.perform(get("/actuator/info"))
            .andExpect(jsonPath("$.app.name").value("Employee Management System"));
        mvc.perform(get("/actuator/metrics"))
            .andExpect(jsonPath("$.names", hasItem("jvm.memory.used")));
        mvc.perform(get("/actuator/caches"))
            .andExpect(jsonPath("$.cacheManagers.cacheManager.caches.employeeCount").exists());
        // Endpoint không nằm trong exposure.include → không mở ra HTTP
        mvc.perform(get("/actuator/env")).andExpect(status().isNotFound());
    }

    @Test
    void heartbeatTask_isScheduled() {
        assertThat(scheduledTaskHolder.getScheduledTasks())
            .anyMatch(task -> task.getTask().toString().contains("SystemHeartbeatTask.logHeartbeat"));
    }
}
