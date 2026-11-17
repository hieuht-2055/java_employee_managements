package com.example.employeemanagement.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Lab 9: cầu nối giữa bảng app_user và Spring Security.
// DaoAuthenticationProvider gọi loadUserByUsername() rồi so mật khẩu bằng PasswordEncoder.
// (≈ Devise: User.find_for_authentication + valid_password?)
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository repository;

    public AppUserDetailsService(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        AppUser user = repository.findByUsername(username)
            // Không nói rõ "sai username" hay "sai mật khẩu" ra ngoài: DaoAuthenticationProvider
            // đổi exception này thành BadCredentialsException chung (chống dò username).
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return User.withUsername(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRole().name())   // → authority "ROLE_ADMIN"
            .build();
    }
}
