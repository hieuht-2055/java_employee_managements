package com.example.employeemanagement.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeemanagement.auth.dto.LoginRequest;
import com.example.employeemanagement.auth.dto.TokenResponse;
import com.example.employeemanagement.auth.dto.UserResponse;
import com.example.employeemanagement.auth.jwt.JwtTokenService;
import com.example.employeemanagement.common.DuplicateResourceException;

// Lab 9: đăng ký (API + trang web dùng chung) và đăng nhập trả JWT.
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService tokenService;

    public AuthService(AppUserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtTokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    // Người tự đăng ký luôn là USER. ADMIN chỉ tạo bằng tay (seeder dev / SQL ở prod).
    @Transactional
    public UserResponse register(String username, String rawPassword) {
        String name = username.trim();
        if (userRepository.existsByUsername(name)) {
            throw new DuplicateResourceException("Username '" + name + "' đã tồn tại");
        }
        AppUser user = userRepository.save(new AppUser(name, passwordEncoder.encode(rawPassword), Role.USER));
        log.info("Registered user id={} username={}", user.getId(), user.getUsername());
        return UserResponse.from(user);
    }

    // Gọi AuthenticationManager thủ công (≈ warden.authenticate!): sai thông tin → BadCredentialsException,
    // GlobalApiExceptionHandler đổi thành 401.
    public TokenResponse login(LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(req.username(), req.password()));
            log.info("Login success username={}", auth.getName());
            return tokenService.issue(auth);
        } catch (AuthenticationException ex) {
            log.warn("Login failed username={}", req.username());
            throw ex;
        }
    }
}
