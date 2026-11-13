package com.example.employeemanagement;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

// Lab 1 + 2: /hello và bean demo. Cùng cấu hình với các test khác → dùng lại Spring context đã cache.
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class HelloAndBeanDemoTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void hello_returnsGreeting() throws Exception {
        mvc.perform(get("/hello"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hello, Employee Management!"));
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
