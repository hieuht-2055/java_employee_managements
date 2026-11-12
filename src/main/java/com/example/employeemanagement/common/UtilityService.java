package com.example.employeemanagement.common;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Lab 2: bean tạo bằng stereotype @Service (component scan tự đăng ký vào IoC container).
// Mặc định scope singleton: cả app dùng chung một instance.
@Service
public class UtilityService {

    private final String employeeCodePrefix;

    // Constructor injection: chỉ có 1 constructor nên không cần @Autowired.
    // ${...:EMP} = đọc app.employee-code-prefix, thiếu thì dùng "EMP".
    public UtilityService(@Value("${app.employee-code-prefix:EMP}") String employeeCodePrefix) {
        this.employeeCodePrefix = employeeCodePrefix;
    }

    // 7 → "EMP0007"
    public String generateEmployeeCode(long sequence) {
        if (sequence <= 0) {
            throw new IllegalArgumentException("sequence phải > 0");
        }
        return "%s%04d".formatted(employeeCodePrefix, sequence);
    }

    // "  nguyễn   văn AN " → "Nguyễn Văn An"  (≈ "...".squish.titleize trong Rails)
    public String formatName(String raw) {
        if (raw == null || raw.isBlank()) {
            return raw;
        }
        return Arrays.stream(raw.trim().split("\\s+"))
            .map(word -> word.substring(0, 1).toUpperCase(Locale.ROOT)
                + word.substring(1).toLowerCase(Locale.ROOT))
            .collect(Collectors.joining(" "));
    }

    // " An@Example.COM " → "an@example.com"
    public String normalizeEmail(String raw) {
        return raw == null ? null : raw.trim().toLowerCase(Locale.ROOT);
    }
}
