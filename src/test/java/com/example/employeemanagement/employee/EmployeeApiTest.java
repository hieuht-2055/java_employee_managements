package com.example.employeemanagement.employee;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagement.TestcontainersConfiguration;
import com.example.employeemanagement.department.Department;
import com.example.employeemanagement.department.DepartmentRepository;

// Lab 4 + 5: test REST API qua MockMvc trên Postgres thật.
// @Transactional: MockMvc chạy cùng thread nên request nằm trong transaction của test → rollback sau mỗi test.
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
class EmployeeApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Long itId;

    @BeforeEach
    void setUp() {
        itId = departmentRepository.save(new Department("Api IT")).getId();
    }

    private ResultActions postEmployee(String json) throws Exception {
        return mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private String body(String name, String email) {
        return """
            {"name": "%s", "email": "%s", "departmentId": %d}""".formatted(name, email, itId);
    }

    @Test
    void create_returns201WithCodeAndDepartment() throws Exception {
        postEmployee(body("  nguyễn văn AN ", "An@Example.com"))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", startsWith("/api/employees/")))
            .andExpect(jsonPath("$.code").value(startsWith("EMP")))
            .andExpect(jsonPath("$.name").value("Nguyễn Văn An"))
            .andExpect(jsonPath("$.email").value("an@example.com"))
            .andExpect(jsonPath("$.departmentName").value("Api IT"));
    }

    @Test
    void create_invalidBody_returns400WithFieldErrors() throws Exception {
        postEmployee("""
            {"name": "  ", "email": "not-an-email"}""")
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.errors.name").value("Tên không được để trống"))
            .andExpect(jsonPath("$.errors.email").value("Email không hợp lệ"))
            .andExpect(jsonPath("$.errors.departmentId").value("Phải chọn phòng ban"));
    }

    @Test
    void create_malformedJson_returns400() throws Exception {
        postEmployee("{\"name\": ")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Request body sai định dạng JSON hoặc sai kiểu dữ liệu"));

        postEmployee("""
            {"name": "An", "email": "an@example.com", "departmentId": "abc"}""")
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_duplicateEmail_returns409() throws Exception {
        postEmployee(body("An", "an@example.com")).andExpect(status().isCreated());
        postEmployee(body("An 2", "AN@example.com"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.detail").value("Email 'an@example.com' đã được sử dụng"));
    }

    @Test
    void create_unknownDepartment_returns404() throws Exception {
        postEmployee("""
            {"name": "An", "email": "an@example.com", "departmentId": 999999}""")
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail").value("Department với id 999999 không tồn tại"));
    }

    @Test
    void get_unknownId_returns404ProblemDetail() throws Exception {
        mvc.perform(get("/api/employees/999999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("Employee với id 999999 không tồn tại"));
    }

    @Test
    void get_nonNumericId_returns400() throws Exception {
        mvc.perform(get("/api/employees/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void updateAndDelete() throws Exception {
        String location = postEmployee(body("An", "an@example.com"))
            .andReturn().getResponse().getHeader("Location");

        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON).content(body("an updated", "an2@example.com")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("An Updated"))
            .andExpect(jsonPath("$.email").value("an2@example.com"));

        mvc.perform(delete(location)).andExpect(status().isNoContent());
        mvc.perform(get(location)).andExpect(status().isNotFound());
        mvc.perform(delete(location)).andExpect(status().isNotFound());
    }

    @Test
    void list_filtersByNameAndDepartment() throws Exception {
        postEmployee(body("Nguyễn Văn An", "an@example.com"));
        postEmployee(body("Lê Bình", "binh@example.com"));

        mvc.perform(get("/api/employees").param("name", "bình").param("departmentId", itId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name").value("Lê Bình"));
    }

    @Test
    void departments_listAndValidateCreate() throws Exception {
        mvc.perform(get("/api/departments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.name == 'IT')]").exists());

        mvc.perform(post("/api/departments").contentType(MediaType.APPLICATION_JSON).content("{\"name\": \"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.name").value("Tên phòng ban không được để trống"));

        mvc.perform(post("/api/departments").contentType(MediaType.APPLICATION_JSON).content("{\"name\": \"it\"}"))
            .andExpect(status().isConflict());
    }
}
