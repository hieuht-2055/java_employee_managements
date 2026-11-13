# Employee Management System (mini project Spring Boot)

Spring Boot 4.1.2 · Java 25 · PostgreSQL 17 · Flyway · Thymeleaf. Đã xong Lab 1–6.

## Chạy

```bash
docker compose up -d       # Postgres ở cổng 5433 (container ems-postgres)
./mvnw spring-boot:run     # http://localhost:8080 — Flyway tự tạo bảng + seed phòng ban
./mvnw test                # 28 test, cần Docker (Testcontainers)
docker compose down -v     # xoá sạch DB
```

## Trang web (Lab 6)

- http://localhost:8080/employees/list
- http://localhost:8080/employees/add
- http://localhost:8080/employees/search

## REST API (Lab 3–5)

```bash
curl localhost:8080/api/departments
curl -i -X POST localhost:8080/api/employees -H 'Content-Type: application/json' \
  -d '{"name":"nguyễn văn an","email":"an@example.com","departmentId":1}'
curl "localhost:8080/api/employees?name=an&departmentId=1"
curl localhost:8080/api/employees/1
curl -X PUT localhost:8080/api/employees/1 -H 'Content-Type: application/json' \
  -d '{"name":"An","email":"an2@example.com","departmentId":2}'
curl -i -X DELETE localhost:8080/api/employees/1

# Lỗi trả về ProblemDetail (application/problem+json)
curl -X POST localhost:8080/api/employees -H 'Content-Type: application/json' -d '{"name":"","email":"x"}'  # 400 + errors
curl localhost:8080/api/employees/999                                                                    # 404
curl -X POST localhost:8080/api/employees -H 'Content-Type: application/json' -d '{"name": '              # 400 JSON hỏng
```

Lab 1–2: `curl localhost:8080/hello`, `curl "localhost:8080/api/demo/password?raw=secret123"`.

## Cấu trúc

```
src/main/java/com/example/employeemanagement/
├── hello/        HelloController                                   # Lab 1
├── common/       UtilityService (Lab 2), GlobalApiExceptionHandler,
│                 ResourceNotFoundException, DuplicateResourceException  # Lab 5
├── config/       AppConfig (@Bean PasswordEncoder)                 # Lab 2
├── demo/         BeanDemoController                                # Lab 2
├── department/   Department, DepartmentRepository, DepartmentService, DepartmentController, DTO
└── employee/     Employee, EmployeeRepository, EmployeeService, EmployeeController (REST),
                  EmployeePageController + EmployeeForm (Thymeleaf), dto/
src/main/resources/
├── db/migration/ V1__create_department_and_employee.sql, V2__seed_departments.sql
├── templates/    fragments/layout.html, employees/{list,form,search}.html, error/404.html, error.html
└── static/css/app.css
```
