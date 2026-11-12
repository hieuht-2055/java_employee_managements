package com.example.employeemanagement.employee;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

// Test tích hợp Lab 1–3: khởi động cả Spring context, gọi API qua MockMvc (không mở cổng thật).
// Repository in-memory là singleton → mỗi test dùng context mới để dữ liệu không dính nhau.
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmployeeApiTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private void createEmployee(String json) throws Exception {
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated());
    }

    @Test
    void hello_returnsGreeting() throws Exception {
        mvc.perform(get("/hello"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello, Employee Management!"));
    }

    @Test
    void create_returns201WithLocationAndGeneratedCode() throws Exception {
        mvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "  nguyễn văn AN ", "email": " An@Example.com", "department": "IT"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/employees/1"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.code").value("EMP0001"))
            .andExpect(jsonPath("$.name").value("Nguyễn Văn An"))
            .andExpect(jsonPath("$.email").value("an@example.com"));
    }

    @Test
    void list_returnsAllAndFiltersByDepartment() throws Exception {
        createEmployee("""
            {"name": "An", "email": "an@example.com", "department": "IT"}""");
        createEmployee("""
            {"name": "Binh", "email": "binh@example.com", "department": "HR"}""");

        mvc.perform(get("/api/employees"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));

        mvc.perform(get("/api/employees").param("department", "it"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name").value("An"));
    }

    @Test
    void getById_returnsEmployeeOr404() throws Exception {
        createEmployee("""
            {"name": "An", "email": "an@example.com", "department": "IT"}""");

        mvc.perform(get("/api/employees/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("EMP0001"));

        mvc.perform(get("/api/employees/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void passwordDemo_usesCustomBcryptBean() throws Exception {
        mvc.perform(get("/api/demo/password").param("raw", "secret123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.encoderClass").value("BCryptPasswordEncoder"))
            .andExpect(jsonPath("$.hash").value(startsWith("$2a$10$")))
            .andExpect(jsonPath("$.matches").value(true));
    }
}
