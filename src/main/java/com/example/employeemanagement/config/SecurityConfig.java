package com.example.employeemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.example.employeemanagement.auth.ProblemDetailSecurityErrorHandler;
import com.example.employeemanagement.auth.jwt.JwtTokenService;

// Lab 9: HAI filter chain, Spring thử theo @Order, chain nào có securityMatcher khớp trước thì dùng chain đó.
//   1. API (/api/**, /actuator/**): JWT, stateless, không CSRF, lỗi trả ProblemDetail JSON.
//   2. Web (mọi URL còn lại): form login + session (≈ Devise), có CSRF, lỗi trả trang HTML.
// Quy tắc authorizeHttpRequests xét từ trên xuống, khớp cái đầu tiên → cụ thể đặt trước, chung đặt sau.
@Configuration
public class SecurityConfig {

    // Dùng chung cho POST /api/auth/login (AuthService) và form login của trang web.
    // Security 7: DaoAuthenticationProvider bắt buộc truyền UserDetailsService qua constructor.
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
                                                      ProblemDetailSecurityErrorHandler errorHandler) throws Exception {
        http
            .securityMatcher("/api/**", "/actuator/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers("/api/demo/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                // USER chỉ được đọc (mọi GET); mọi thao tác ghi còn lại chỉ ADMIN
                .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().hasRole("ADMIN"))
            .csrf(csrf -> csrf.disable())   // API không dùng cookie session → không có nguy cơ CSRF
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(errorHandler))
            .exceptionHandling(e -> e
                .authenticationEntryPoint(errorHandler)
                .accessDeniedHandler(errorHandler));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http,
                                                      AuthenticationManager authenticationManager) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // /error phải mở: trang 404/500 được render qua lượt dispatch thứ hai tới /error,
                // nếu bị chặn thì người chưa đăng nhập bị đá về /login thay vì thấy trang lỗi.
                .requestMatchers("/login", "/register", "/hello", "/css/**", "/error").permitAll()
                .requestMatchers("/employees/add").hasRole("ADMIN")
                .anyRequest().authenticated())
            .authenticationManager(authenticationManager)
            .formLogin(form -> form
                .loginPage("/login")                              // trang tự viết (templates/auth/login.html)
                .defaultSuccessUrl("/employees/list", true))
            .logout(logout -> logout.logoutSuccessUrl("/login?logout"));   // POST /logout (có CSRF)
        return http.build();
    }

    // JWT {"roles": ["ADMIN"]} → authority ROLE_ADMIN để hasRole("ADMIN") khớp.
    // Mặc định converter đọc claim "scope"/"scp" và thêm tiền tố SCOPE_.
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName(JwtTokenService.ROLES_CLAIM);
        authorities.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
}
