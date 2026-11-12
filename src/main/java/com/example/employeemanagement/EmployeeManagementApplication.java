package com.example.employeemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication = @SpringBootConfiguration + @EnableAutoConfiguration + @ComponentScan
// Component scan quét package này và mọi package con (hello, common, config, demo, employee).
@SpringBootApplication
public class EmployeeManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }
}
