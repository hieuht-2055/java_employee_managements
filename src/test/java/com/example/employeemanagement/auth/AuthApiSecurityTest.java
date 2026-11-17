package com.example.employeemanagement.auth;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagement.TestcontainersConfiguration;
import com.example.employeemanagement.department.Department;
import com.example.employeemanagement.department.DepartmentRepository;

import tools.jackson.databind.ObjectMapper;

// Lab 9: test bảo mật REST API với JWT THẬT (đăng nhập → lấy token → gọi API), không dùng @WithMockUser.
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class AuthApiSecurityTest {

    @Autowired private MockMvc mvc;
    @Autowired private AppUserRepository userRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long departmentId;

    @BeforeEach
    void setUp() {
        userRepository.save(new AppUser("t_admin", passwordEncoder.encode("admin-pass-1"), Role.ADMIN));
        userRepository.save(new AppUser("t_user", passwordEncoder.encode("user-pass-1"), Role.USER));
        departmentId = departmentRepository.save(new Department("Sec IT")).getId();
    }

    private ResultActions postJson(String url, String json) throws Exception {
        return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String body = postJson("/api/auth/login", """
            {"username": "%s", "password": "%s"}""".formatted(username, password))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("accessToken").asString();
    }

    private String employeeJson(String email) {
        return """
            {"name": "Sec Test", "email": "%s", "departmentId": %d}""".formatted(email, departmentId);
    }

    @Test
    void register_createsUserRoleAndNeverReturnsPassword() throws Exception {
        postJson("/api/auth/register", """
            {"username": "new_user", "password": "secret-123"}""")
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("new_user"))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.password").doesNotExist());

        // Đăng nhập được bằng tài khoản vừa tạo
        loginAndGetToken("new_user", "secret-123");
    }

    @Test
    void register_duplicateOrInvalid_isRejected() throws Exception {
        postJson("/api/auth/register", """
            {"username": "t_user", "password": "secret-123"}""")
            .andExpect(status().isConflict());

        postJson("/api/auth/register", """
            {"username": "a b", "password": "short"}""")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.username").exists())
            .andExpect(jsonPath("$.errors.password").value("Mật khẩu dài 8–72 ký tự"));
    }

    @Test
    void login_wrongPassword_returns401ProblemDetail() throws Exception {
        postJson("/api/auth/login", """
            {"username": "t_user", "password": "wrong-password"}""")
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.detail").value("Sai username hoặc mật khẩu"));
    }

    @Test
    void login_returnsBearerTokenWithRoles() throws Exception {
        postJson("/api/auth/login", """
            {"username": "t_admin", "password": "admin-pass-1"}""")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").value(3600))
            // Đúng 1 phần tử: không lẫn "FACTOR_PASSWORD" mà Security 7 tự thêm sau khi xác thực mật khẩu
            .andExpect(jsonPath("$.roles", contains("ADMIN")));
    }

    @Test
    void me_readsIdentityFromToken() throws Exception {
        String token = loginAndGetToken("t_user", "user-pass-1");
        mvc.perform(get("/api/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("t_user"))
            .andExpect(jsonPath("$.authorities", hasItem("ROLE_USER")));
    }

    @Test
    void withoutToken_returns401ProblemDetailFromFilter() throws Exception {
        mvc.perform(get("/api/employees"))
            .andExpect(status().isUnauthorized())
            .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"))
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.instance").value("/api/employees"));
    }

    @Test
    void tamperedToken_returns401() throws Exception {
        String token = loginAndGetToken("t_user", "user-pass-1");
        String tampered = token.substring(0, token.length() - 2) + (token.endsWith("A") ? "BB" : "AA");
        mvc.perform(get("/api/employees").header(HttpHeaders.AUTHORIZATION, "Bearer " + tampered))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.detail").value("Token không hợp lệ hoặc đã hết hạn"));
    }

    @Test
    void user_canReadButCannotWrite() throws Exception {
        String bearer = "Bearer " + loginAndGetToken("t_user", "user-pass-1");

        mvc.perform(get("/api/employees").header(HttpHeaders.AUTHORIZATION, bearer))
            .andExpect(status().isOk());
        mvc.perform(get("/api/reports/statistics").header(HttpHeaders.AUTHORIZATION, bearer))
            .andExpect(status().isOk());

        mvc.perform(post("/api/employees").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content(employeeJson("u@sec.com")))
            .andExpect(status().isForbidden())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.detail").value("Bạn không có quyền thực hiện thao tác này"));
        mvc.perform(post("/api/departments").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\": \"X\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void admin_canCreateEmployee() throws Exception {
        String bearer = "Bearer " + loginAndGetToken("t_admin", "admin-pass-1");
        mvc.perform(post("/api/employees").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content(employeeJson("a@sec.com")))
            .andExpect(status().isCreated());
    }

    @Test
    void actuator_healthIsPublic_restRequiresAdmin() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
        mvc.perform(get("/actuator/metrics")).andExpect(status().isUnauthorized());

        String userBearer = "Bearer " + loginAndGetToken("t_user", "user-pass-1");
        mvc.perform(get("/actuator/metrics").header(HttpHeaders.AUTHORIZATION, userBearer))
            .andExpect(status().isForbidden());

        String adminBearer = "Bearer " + loginAndGetToken("t_admin", "admin-pass-1");
        mvc.perform(get("/actuator/metrics").header(HttpHeaders.AUTHORIZATION, adminBearer))
            .andExpect(status().isOk());
    }

    @Test
    void demoEndpointsStayPublic() throws Exception {
        mvc.perform(get("/api/demo/format").param("name", "an")).andExpect(status().isOk());
    }
}
