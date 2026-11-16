# Employee Management System (mini project Spring Boot)

Spring Boot 4.1.2 · Java 25 · PostgreSQL 17 · Flyway · Thymeleaf · Actuator · Caffeine. Đã xong Lab 1–8.

## Chạy

```bash
docker compose up -d       # Postgres ở cổng 5433 (container ems-postgres)
./mvnw spring-boot:run     # profile mặc định dev: http://localhost:8080, seed 5 nhân viên mẫu khi bảng trống
./mvnw test                # 32 test, cần Docker (Testcontainers)
docker compose down -v     # xoá sạch DB
```

### Profile (Lab 7)

| | `dev` (mặc định) | `prod` |
|---|---|---|
| DB | `localhost:5433/employee_db` | biến môi trường `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (bắt buộc) |
| Log | DEBUG + SQL ra console | INFO, ghi thêm `logs/employee-management.log` |
| Dữ liệu mẫu | Có (`DevSampleDataSeeder`) | Không |
| `/actuator/health` | Hiện chi tiết | Chỉ `status` |

```bash
./mvnw package -DskipTests
SPRING_PROFILES_ACTIVE=prod DB_URL=jdbc:postgresql://localhost:5433/employee_db \
  DB_USERNAME=dev DB_PASSWORD=dev java -jar target/employee-management-0.0.1-SNAPSHOT.jar
```

### Lab 8: báo cáo, Actuator, scheduled task

```bash
curl localhost:8080/api/reports/employee-count   # cache 60s: generatedAt giữ nguyên trong 1 phút
curl localhost:8080/actuator/health
curl localhost:8080/actuator/info
curl localhost:8080/actuator/metrics/http.server.requests
curl localhost:8080/actuator/caches
```

Console in `System running` mỗi 30 giây (đổi bằng `--app.heartbeat-interval=5s`).

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
│                 SystemHeartbeatTask                               # Lab 8
├── config/       AppConfig (@Bean PasswordEncoder, Lab 2), DevSampleDataSeeder (Lab 7),
│                 CacheConfig, SchedulingConfig                     # Lab 8
├── demo/         BeanDemoController                                # Lab 2
├── department/   Department, DepartmentRepository, DepartmentService, DepartmentController, DTO
├── employee/     Employee, EmployeeRepository, EmployeeService, EmployeeController (REST),
│                 EmployeePageController + EmployeeForm (Thymeleaf), dto/
└── report/       EmployeeReportService (@Cacheable), EmployeeReportController   # Lab 8
src/main/resources/
├── application.yml, application-dev.yml, application-prod.yml   # Lab 7
├── db/migration/ V1__create_department_and_employee.sql, V2__seed_departments.sql
├── templates/    fragments/layout.html, employees/{list,form,search}.html, error/404.html, error.html
└── static/css/app.css
```
