# Employee Management System (mini project Spring Boot)

Spring Boot 4.1.2 · Java 25 · Maven wrapper. Đã xong Lab 1–3 (dữ liệu in-memory, mất khi restart).

## Chạy

```bash
./mvnw spring-boot:run     # http://localhost:8080
./mvnw test                # chạy test
```

## Thử nhanh

```bash
# Lab 1
curl localhost:8080/hello
curl "localhost:8080/hello?name=Hieu"

# Lab 2: bean @Service và @Bean
curl "localhost:8080/api/demo/password?raw=secret123"
curl "localhost:8080/api/demo/format?name=nguyen%20van%20AN"

# Lab 3: REST API nhân viên
curl -i -X POST localhost:8080/api/employees -H 'Content-Type: application/json' \
  -d '{"name":"nguyễn văn an","email":"An@Example.com","department":"IT"}'
curl localhost:8080/api/employees
curl "localhost:8080/api/employees?department=IT"
curl -i localhost:8080/api/employees/1
curl -i localhost:8080/api/employees/999     # 404
```

## Cấu trúc

```
src/main/java/com/example/employeemanagement/
├── EmployeeManagementApplication.java
├── hello/HelloController.java              # Lab 1
├── common/UtilityService.java              # Lab 2: @Service
├── config/AppConfig.java                   # Lab 2: @Bean PasswordEncoder
├── demo/BeanDemoController.java            # Lab 2: inject bean
└── employee/                               # Lab 3
    ├── Employee.java
    ├── InMemoryEmployeeRepository.java
    ├── EmployeeService.java
    ├── EmployeeController.java
    └── dto/{CreateEmployeeRequest,EmployeeResponse}.java
```
