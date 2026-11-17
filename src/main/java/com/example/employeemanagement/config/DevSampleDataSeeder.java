package com.example.employeemanagement.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.employeemanagement.auth.AppUser;
import com.example.employeemanagement.auth.AppUserRepository;
import com.example.employeemanagement.auth.Role;
import com.example.employeemanagement.department.DepartmentResponse;
import com.example.employeemanagement.department.DepartmentService;
import com.example.employeemanagement.employee.EmployeeRepository;
import com.example.employeemanagement.employee.EmployeeService;
import com.example.employeemanagement.employee.dto.EmployeeRequest;

// Lab 7: dữ liệu mẫu CHỈ cho profile dev (≈ db/seeds.rb, nhưng tự chạy lúc khởi động).
// @Profile("dev"): bean chỉ được tạo khi profile dev bật → prod và test không bao giờ chạy.
// ApplicationRunner chạy một lần sau khi context khởi động xong (Flyway đã migrate).
// Lab 9: thêm tài khoản mẫu admin/admin123 (ADMIN) và user/user1234 (USER).
@Component
@Profile("dev")
public class DevSampleDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevSampleDataSeeder.class);

    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DevSampleDataSeeder(EmployeeRepository employeeRepository, EmployeeService employeeService,
                               DepartmentService departmentService, AppUserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedUser("admin", "admin123", Role.ADMIN);
        seedUser("user", "user1234", Role.USER);
        seedEmployees();
    }

    private void seedUser(String username, String rawPassword, Role role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        userRepository.save(new AppUser(username, passwordEncoder.encode(rawPassword), role));
        log.info("Seeded {} account '{}' for profile dev", role, username);
    }

    private void seedEmployees() {
        if (employeeRepository.count() > 0) {
            log.debug("Skip seeding employees: table is not empty");
            return;
        }
        List<DepartmentResponse> departments = departmentService.list();
        String[][] samples = {
            {"Nguyễn Văn An", "an@example.com"},
            {"Trần Thị Bình", "binh@example.com"},
            {"Lê Văn Cường", "cuong@example.com"},
            {"Phạm Thị Dung", "dung@example.com"},
            {"Hoàng Văn Em", "em@example.com"},
        };
        for (int i = 0; i < samples.length; i++) {
            Long departmentId = departments.get(i % departments.size()).id();
            employeeService.create(new EmployeeRequest(samples[i][0], samples[i][1], departmentId));
        }
        log.info("Seeded {} sample employees for profile dev", samples.length);
    }
}
