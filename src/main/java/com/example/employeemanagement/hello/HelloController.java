package com.example.employeemanagement.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Lab 1: API kiểm tra project chạy được.
// GET /hello            → "Hello, Employee Management!"
// GET /hello?name=Hieu  → "Hello, Hieu!"
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "Employee Management") String name) {
        return "Hello, " + name + "!";
    }
}
