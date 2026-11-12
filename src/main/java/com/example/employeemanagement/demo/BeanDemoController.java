package com.example.employeemanagement.demo;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.employeemanagement.common.UtilityService;

// Lab 2: inject bean @Bean (PasswordEncoder) và bean @Service (UtilityService) vào controller.
// Endpoint chỉ để xem bean hoạt động; Lab 9 sẽ dùng PasswordEncoder thật khi đăng ký user.
@RestController
@RequestMapping("/api/demo")
public class BeanDemoController {

    private final PasswordEncoder passwordEncoder;
    private final UtilityService utilityService;

    public BeanDemoController(PasswordEncoder passwordEncoder, UtilityService utilityService) {
        this.passwordEncoder = passwordEncoder;
        this.utilityService = utilityService;
    }

    // GET /api/demo/password?raw=secret123
    @GetMapping("/password")
    public Map<String, Object> encodePassword(@RequestParam String raw) {
        String hash = passwordEncoder.encode(raw);
        return Map.of(
            "encoderClass", passwordEncoder.getClass().getSimpleName(),
            "hash", hash,
            "matches", passwordEncoder.matches(raw, hash));
    }

    // GET /api/demo/format?name=  nguyễn   văn AN
    @GetMapping("/format")
    public Map<String, String> formatName(@RequestParam String name) {
        return Map.of("raw", name, "formatted", utilityService.formatName(name));
    }
}
