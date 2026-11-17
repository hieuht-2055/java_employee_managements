# Employee Management System (mini project Spring Boot)

Spring Boot 4.1.2 · Java 25 · PostgreSQL 17 · Flyway · Thymeleaf · Actuator · Caffeine · Spring Security 7 + JWT. Đã xong cả 10 lab.

## Chạy

```bash
docker compose up -d       # Postgres ở cổng 5433 (container ems-postgres)
./mvnw spring-boot:run     # profile mặc định dev: http://localhost:8080 → trang đăng nhập
                           # tài khoản mẫu: admin/admin123 (ADMIN), user/user1234 (USER)
./mvnw test                # 55 test, cần Docker (Testcontainers)
docker compose down -v     # xoá sạch DB
```

### Profile (Lab 7)

| | `dev` (mặc định) | `prod` |
|---|---|---|
| DB | `localhost:5433/employee_db` | biến môi trường `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (bắt buộc) |
| Log | DEBUG + SQL ra console | INFO, ghi thêm `logs/employee-management.log` |
| Dữ liệu mẫu | Có (`DevSampleDataSeeder`) | Không |
| `/actuator/health` | Hiện chi tiết | Chỉ `status` |
| JWT secret | có sẵn trong `application-dev.yml` | biến môi trường `JWT_SECRET` (bắt buộc, ≥ 32 byte) |
| Tài khoản mẫu | admin, user | Không (tạo ADMIN bằng tay) |

```bash
./mvnw package -DskipTests
SPRING_PROFILES_ACTIVE=prod DB_URL=jdbc:postgresql://localhost:5433/employee_db \
  DB_USERNAME=dev DB_PASSWORD=dev JWT_SECRET=$(openssl rand -base64 48) java -jar target/employee-management-0.0.1-SNAPSHOT.jar
```

### Lab 9: đăng ký, đăng nhập, phân quyền

| | Khách | USER | ADMIN |
|---|---|---|---|
| `/login`, `/register`, `POST /api/auth/*`, `/actuator/health` | ✓ | ✓ | ✓ |
| Xem danh sách, tìm kiếm, thống kê (web + `GET /api/**`) | ✗ | ✓ | ✓ |
| Thêm/sửa/xoá (`/employees/add`, `POST/PUT/DELETE /api/**`) | ✗ | ✗ (403) | ✓ |
| `/actuator/metrics`, `/actuator/caches` | ✗ | ✗ | ✓ |

```bash
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}' | python3 -c 'import sys,json;print(json.load(sys.stdin)["accessToken"])')
curl -H "Authorization: Bearer $TOKEN" localhost:8080/api/employees
curl -H "Authorization: Bearer $TOKEN" localhost:8080/api/auth/me
curl -X POST localhost:8080/api/auth/register -H 'Content-Type: application/json' \
  -d '{"username":"hieu","password":"hieu12345"}'     # luôn ra role USER
curl localhost:8080/api/employees                     # 401 ProblemDetail
```

Các lệnh curl ở mục "REST API" bên dưới cần thêm `-H "Authorization: Bearer $TOKEN"`.

### Lab 10: thống kê

- Trang: http://localhost:8080/employees/statistics
- API: `curl -H "Authorization: Bearer $TOKEN" localhost:8080/api/reports/statistics`

### Lab 8: báo cáo, Actuator, scheduled task

```bash
curl -H "Authorization: Bearer $TOKEN" localhost:8080/api/reports/employee-count   # cache 60s: generatedAt giữ nguyên trong 1 phút
curl localhost:8080/actuator/health
curl localhost:8080/actuator/info
curl -H "Authorization: Bearer $TOKEN" localhost:8080/actuator/metrics/http.server.requests   # chỉ ADMIN
curl -H "Authorization: Bearer $TOKEN" localhost:8080/actuator/caches
```

Console in `System running` mỗi 30 giây (đổi bằng `--app.heartbeat-interval=5s`).

## Trang web (Lab 6)

- http://localhost:8080/employees/list
- http://localhost:8080/employees/add
- http://localhost:8080/employees/search
- http://localhost:8080/employees/statistics

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
├── auth/         AppUser, Role, AppUserRepository, AppUserDetailsService, AuthService,
│                 AuthController (API), AuthPageController + RegisterForm (web),
│                 ProblemDetailSecurityErrorHandler, dto/, jwt/     # Lab 9
├── config/       AppConfig (@Bean PasswordEncoder, Lab 2), DevSampleDataSeeder (Lab 7),
│                 CacheConfig, SchedulingConfig (Lab 8), SecurityConfig (Lab 9)
├── demo/         BeanDemoController                                # Lab 2
├── department/   Department, DepartmentRepository, DepartmentService, DepartmentController, DTO
├── employee/     Employee, EmployeeRepository, EmployeeService, EmployeeController (REST),
│                 EmployeePageController + EmployeeForm (Thymeleaf), dto/
└── report/       EmployeeReportService (@Cacheable, Lab 8), EmployeeReportController,
                  EmployeeStatisticsRepository/Service, StatisticsPageController   # Lab 10
src/main/resources/
├── application.yml, application-dev.yml, application-prod.yml   # Lab 7
├── db/migration/ V1__create_department_and_employee.sql, V2__seed_departments.sql, V3__create_app_user.sql
├── templates/    fragments/layout.html, auth/{login,register}.html,
│                 employees/{list,form,search,statistics}.html, error/{403,404}.html, error.html
└── static/css/app.css
```
