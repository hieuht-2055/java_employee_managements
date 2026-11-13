package com.example.employeemanagement.employee;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagement.TestcontainersConfiguration;
import com.example.employeemanagement.department.Department;
import com.example.employeemanagement.department.DepartmentRepository;
import com.example.employeemanagement.employee.dto.EmployeeRequest;

// Lab 6: test trang Thymeleaf (view name, model, HTML render ra, redirect + flash).
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
class EmployeePageTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeService employeeService;

    private Long itId;
    private Long hrId;

    @BeforeEach
    void setUp() {
        itId = departmentRepository.save(new Department("Page IT")).getId();
        hrId = departmentRepository.save(new Department("Page HR")).getId();
        employeeService.create(new EmployeeRequest("Nguyễn Văn An", "an@page.com", itId));
        employeeService.create(new EmployeeRequest("Trần Bình", "binh@page.com", hrId));
    }

    @Test
    void list_rendersEmployeesFromDb() throws Exception {
        mvc.perform(get("/employees/list"))
            .andExpect(status().isOk())
            .andExpect(view().name("employees/list"))
            .andExpect(content().string(containsString("Nguyễn Văn An")))
            .andExpect(content().string(containsString("Page HR")));
    }

    @Test
    void addForm_rendersDepartmentOptions() throws Exception {
        mvc.perform(get("/employees/add"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("employeeForm", "departments"))
            .andExpect(content().string(containsString("Page IT")));
    }

    @Test
    void add_invalid_rerendersFormWithErrors() throws Exception {
        mvc.perform(post("/employees/add").param("name", "").param("email", "abc").param("departmentId", ""))
            .andExpect(status().isOk())
            .andExpect(view().name("employees/form"))
            .andExpect(model().attributeHasFieldErrors("employeeForm", "name", "email", "departmentId"))
            .andExpect(content().string(containsString("Email không hợp lệ")));
    }

    @Test
    void add_duplicateEmail_showsErrorOnEmailField() throws Exception {
        mvc.perform(post("/employees/add")
                .param("name", "An khác").param("email", "AN@page.com").param("departmentId", itId.toString()))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrorCode("employeeForm", "email", "duplicate"));
    }

    @Test
    void add_valid_redirectsToListWithFlash() throws Exception {
        mvc.perform(post("/employees/add")
                .param("name", "lê văn cường").param("email", "cuong@page.com").param("departmentId", hrId.toString()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/employees/list"))
            .andExpect(flash().attribute("message", containsString("Lê Văn Cường")));
    }

    @Test
    void search_filtersByNameAndDepartment() throws Exception {
        mvc.perform(get("/employees/search").param("name", "bình").param("departmentId", hrId.toString()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Trần Bình")))
            .andExpect(content().string(not(containsString("Nguyễn Văn An"))));
    }

    @Test
    void search_withoutParams_showsOnlyForm() throws Exception {
        mvc.perform(get("/employees/search"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("searched", false))
            .andExpect(content().string(not(containsString("Kết quả"))));
    }
}
