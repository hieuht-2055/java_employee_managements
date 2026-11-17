package com.example.employeemanagement.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagement.TestcontainersConfiguration;

// Lab 9: bảo mật trang web (form login + session + CSRF).
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class WebSecurityTest {

    @Autowired private MockMvc mvc;
    @Autowired private AppUserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.save(new AppUser("w_user", passwordEncoder.encode("user-pass-1"), Role.USER));
    }

    @Test
    void anonymous_isRedirectedToLogin() throws Exception {
        mvc.perform(get("/employees/list"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));   // Security 6+/7 redirect bằng URL tương đối
    }

    @Test
    void loginAndRegisterPagesArePublic() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isOk()).andExpect(content().string(containsString("_csrf")));
        mvc.perform(get("/register")).andExpect(status().isOk());
    }

    @Test
    void formLogin_successAndFailure() throws Exception {
        mvc.perform(formLogin("/login").user("w_user").password("user-pass-1"))
            .andExpect(authenticated().withUsername("w_user").withRoles("USER"))
            .andExpect(redirectedUrl("/employees/list"));

        mvc.perform(formLogin("/login").user("w_user").password("wrong"))
            .andExpect(unauthenticated())
            .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void loginWithoutCsrfToken_isRejected() throws Exception {
        mvc.perform(post("/login").param("username", "w_user").param("password", "user-pass-1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void user_canViewButCannotOpenAddPage() throws Exception {
        mvc.perform(get("/employees/list"))
            .andExpect(status().isOk())
            .andExpect(content().string(not(containsString("+ Thêm nhân viên"))));
        mvc.perform(get("/employees/statistics")).andExpect(status().isOk());
        mvc.perform(get("/employees/add")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_seesAddLink() throws Exception {
        mvc.perform(get("/employees/list"))
            .andExpect(content().string(containsString("+ Thêm nhân viên")));
        mvc.perform(get("/employees/add")).andExpect(status().isOk());
    }

    @Test
    void registerPage_createsUserAccount() throws Exception {
        mvc.perform(post("/register").with(csrf())
                .param("username", "web_new").param("password", "secret-123").param("confirmPassword", "secret-123"))
            .andExpect(redirectedUrl("/login"));
        assertThat(userRepository.findByUsername("web_new"))
            .hasValueSatisfying(u -> {
                assertThat(u.getRole()).isEqualTo(Role.USER);
                assertThat(u.getPassword()).startsWith("$2a$").isNotEqualTo("secret-123");
            });
    }

    @Test
    void registerPage_mismatchedPassword_showsError() throws Exception {
        mvc.perform(post("/register").with(csrf())
                .param("username", "web_new2").param("password", "secret-123").param("confirmPassword", "other-123"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Mật khẩu nhập lại không khớp")));
    }

    @Test
    @WithMockUser
    void logout_redirectsToLoginPage() throws Exception {
        mvc.perform(logout()).andExpect(redirectedUrl("/login?logout"));
    }
}
