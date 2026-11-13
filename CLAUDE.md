# Bối cảnh học Spring Boot (gộp từ các cuộc trò chuyện trước)

> File này dành cho Claude (Claude Code / Claude Desktop) đọc khi bắt đầu phiên mới.
> Bản này đặt ở gốc mini project `~/environment/java_employee_managements/CLAUDE.md` (mục 2 và 9 đã cập nhật cho project này).
> Cập nhật lần cuối: 12/11/2026.

---

## 1. Về mình và cách trả lời

- Lập trình viên **Rails** lâu năm, từng làm Python, Node.js, TypeScript/React. Mới học Java/Spring.
- Trả lời bằng **tiếng Việt**, ngắn gọn, và **so sánh với Rails** (hoặc NestJS/TS khi hợp hơn).
- Mình học qua **slide của Sun\*** (gửi ảnh chụp slide). Mỗi lần cần:
  1. Giải thích slide.
  2. **Ví dụ chạy được** trong playground.
  3. Chỉ ra chỗ slide **sai / lỗi thời** so với Boot 4 / Spring 7 / Spring Security 7 / Hibernate 7.
- Khi mình dán log lỗi: đọc dòng `Caused by:` **cuối cùng** trước, giải thích nguyên nhân gốc, đưa lệnh sửa cụ thể.
- Khi sửa playground: ưu tiên đưa **file đầy đủ** (mình từng dán thiếu / sai chỗ khi chỉ nhận đoạn code rời).

---

## 2. Môi trường

| Thành phần | Giá trị |
|---|---|
| OS | WSL Ubuntu (trình duyệt Windows truy cập `localhost:8080` được) |
| Java | 25 (Temurin, qua SDKMAN) |
| Spring Boot | **4.1.2** (Spring Framework 7.0.10, Spring Data JPA 4.1.2, Hibernate 7.4.13, Spring Security 7, HikariCP 7) |
| Build | Maven wrapper `./mvnw` (có `spring-boot-starter-parent` → cờ `-parameters` đã bật) |
| DB | PostgreSQL 17 qua Docker Compose (`compose.yaml`, container `ems-postgres`, **cổng 5433** để không đụng playground 5432), database `employee_db`, user `dev` / password `dev`. Schema do Flyway quản lý (`db/migration`), `ddl-auto: validate` |
| Project | `~/environment/java_employee_managements`, artifact `employee-management`, package `com.example.employeemanagement` |
| Playground (tham khảo) | `~/environment/java-spring-learning`, package `com.example.playground` |

**Dependency đang có trong `pom.xml`** (Boot 4 đã đổi tên một số starter):

```xml
spring-boot-starter-webmvc        <!-- Boot 3: spring-boot-starter-web -->
spring-boot-starter-data-jpa
org.postgresql:postgresql (runtime)
spring-boot-starter-validation
spring-boot-starter-security
spring-boot-starter-thymeleaf
spring-boot-starter-aspectj       <!-- Boot 3: spring-boot-starter-aop -->
```

Xem `playground-snapshot/pom-dependencies.xml`. Tuỳ chọn: `spring-boot-docker-compose` (runtime, optional) để Boot tự `docker compose up`.

**Lệnh hay dùng**

```bash
docker compose up -d                 # bật Postgres (sau mỗi lần khởi động lại máy/WSL)
./mvnw spring-boot:run               # chạy
./mvnw clean spring-boot:run         # khi đổi pom.xml
./mvnw dependency:tree | grep -i <tên>
docker exec -it ems-postgres psql -U dev -d employee_db   # ≈ rails dbconsole
docker compose down -v               # reset sạch DB (≈ rails db:drop)
```

---

## 3. Quy ước playground (CHỈ áp dụng cho playground, KHÔNG áp dụng cho mini project)

- Mọi ví dụ Java nằm trong **MỘT file**: `src/main/java/com/example/playground/PlaygroundApplication.java`.
  Các class phụ là **top-level, package-private** trong cùng file (KHÔNG lồng class trong class).
- Ngoại lệ bắt buộc: template Thymeleaf (`src/main/resources/templates/...`) và file tĩnh (`src/main/resources/static/...`).
- `spring.jpa.hibernate.ddl-auto: create` → mỗi lần chạy tạo lại bảng + seed lại dữ liệu (chỉ dùng cho playground).
- Endpoint demo đặt dưới `/api/demo/**` (permitAll, không cần đăng nhập). Trang HTML đặt dưới `/web/**` (permitAll).
- Snapshot đầy đủ hiện tại: thư mục `playground-snapshot/`.

---

## 4. Kế hoạch

- Học Spring 7 ngày (thứ 2–6, 5–6h/ngày): **17/11 → 25/11/2026**.
- Mini project **"Employee Management System"** (10 lab), làm cùng Claude agent ở ~4 ngày cuối:
  1. Hello API · 2. Bean/IoC · 3. REST API · 4. JPA + Postgres (employee, department, tìm kiếm)
  5. Validation & Exception · 6. Thymeleaf · 7. Logging & Profiles · 8. Actuator/Scheduling/Caching
  9. Spring Security + JWT (USER xem, ADMIN CRUD) · 10. Thống kê (`@Query`, `/employees/statistics`).

---

## 5. Tiến độ: ĐÃ HỌC XONG

### Nền tảng (cuộc trò chuyện đầu)
1. Spring tổng quan, các module, lợi ích.
2. IoC, DIP, DI, IoC Container (ApplicationContext vs BeanFactory).
3. Bean: 4 cách tạo, `@Bean` vs `@Component`, `@Configuration` (proxy giữ singleton), scope, bẫy prototype trong singleton (`ObjectProvider`), `@Lazy`, `@DependsOn`, `@Primary`/`@Qualifier`/`@Profile`, inject List/Map, `@Value`/`@ConfigurationProperties`, bean inheritance, `@Import`/`@ImportResource`.
4. DI: constructor (khuyên dùng) / setter / field.
5. Annotation là metadata, đọc bằng reflection (khác Ruby macro / Python decorator); retention SOURCE/CLASS/RUNTIME.
6. Stereotype: `@Component`, `@Service`, `@Repository`, `@Controller`, `@Configuration`.
7. AOP: join point, pointcut, advice (5 loại), aspect, proxy, weaving, thứ tự advice, **self-invocation không qua proxy**.
8. JDBC: Connection → Statement → ResultSet, PreparedStatement.
9. ORM: impedance mismatch; Active Record (Rails) vs Data Mapper (JPA).
10. JPA: Persistence Unit, EntityManagerFactory, EntityManager (persistence context, dirty checking), JPQL, `javax` → `jakarta`.

### Spring Data JPA
11. Repository pattern, marker interface `Repository<T, ID>`, cây `Repository → CrudRepository → PagingAndSortingRepository → JpaRepository` (Spring Data 3+: PagingAndSorting **không còn** extends Crud), `SimpleJpaRepository`, proxy tự sinh implementation (reflection đọc tên method + dynamic proxy; ≈ `method_missing` nhưng parse lúc khởi động).
12. Query methods: derived query, `@NamedQuery`, `@Query` (JPQL) vs `@NativeQuery` (= `@Query(nativeQuery=true)`, từ 3.4), `QueryRewriter`, advanced LIKE `%:x%`, `Sort`, named params, SpEL `#{#entityName}`, projection, paging (trang từ 0).
13. `@Modifying`: derived delete (SELECT rồi remove từng cái, có callback ≈ `destroy_all`) vs bulk JPQL (1 câu, bỏ callback ≈ `delete_all`/`update_all`); `clearAutomatically`/`flushAutomatically`.
14. Custom repository fragment (`XxxCustom` + `XxxCustomImpl`), Criteria API; nhắc tới `Specification` cho tìm kiếm nhiều filter.
15. Entity mapping: `@Entity/@Table/@Id/@GeneratedValue/@Column/@Transient`, quan hệ `@ManyToOne/@OneToMany/@OneToOne/@ManyToMany/@JoinColumn/@JoinTable`, **owning side**, helper đồng bộ 2 chiều, cascade + `orphanRemoval`, fetch (ToOne mặc định EAGER → nên LAZY), `LazyInitializationException`, `@EntityGraph` (≈ `includes`), `@ElementCollection`, `@Version` (≈ `lock_version`).
16. Transactionality của repository: `SimpleJpaRepository` class-level `readOnly=true`, method ghi override; `readOnly` (tắt dirty checking, Postgres từ chối ghi). Auditing: `@EnableJpaAuditing`, `@EntityListeners(AuditingEntityListener.class)`, `AuditorAware` (≈ `Current.user`), bulk update không kích hoạt auditing.

### Spring Boot & Web
17. REST: `@RestController`, `@RequestMapping` (không khai báo method = khớp MỌI method), `@Get/Post/Put/Patch/DeleteMapping`, `@PathVariable`, `@RequestParam` (mặc định bắt buộc), `@RequestBody`, `@RequestHeader`, `@ModelAttribute` (bind query/form vào object, KHÔNG đọc JSON), `ResponseEntity` (201 + Location, 204), DTO record, PUT vs PATCH.
18. Starters & auto-configuration: `@SpringBootApplication` = `@SpringBootConfiguration` + `@EnableAutoConfiguration` + `@ComponentScan`; `@ConditionalOnClass/OnProperty/OnMissingBean`; file `AutoConfiguration.imports` trong từng JAR (Boot 4 tách module); `--debug` → Conditions Evaluation Report; component scan chỉ quét package gốc + con.
19. Annotation vs XML (ưu/nhược).
20. `application.yml`/`.properties`, profile (multi-document `---`, `application-{profile}.yml`), thứ tự ưu tiên (CLI > env > profile > mặc định), relaxed binding, `${DB_PASSWORD:dev}`.
21. Exception handling: thứ tự xử lý (local `@ExceptionHandler` → `@RestControllerAdvice` theo `@Order` → resolver mặc định → `/error`), `ProblemDetail` (RFC 9457), kế thừa `ResponseEntityExceptionHandler`, bẫy catch-all, advice không bắt lỗi trong Filter, giới hạn phạm vi advice (`assignableTypes`, `basePackages`, `annotations`), log 4xx/5xx.
22. Validation: Bean Validation, `@NotBlank/@Size/@Min/@Max/@Pattern`, `@Valid`; 3 exception: `MethodArgumentNotValidException`, `HandlerMethodValidationException` (Spring 6.1+, ràng buộc trực tiếp trên `@RequestParam`), `ConstraintViolationException` (`@Validated` ở service, PHẢI tự bắt).

### Spring Security (Lab 9 chuẩn bị)
23. Filter chain trước DispatcherServlet; `DelegatingFilterProxy` → `FilterChainProxy` → `SecurityFilterChain`; secure-by-default; lambda DSL (Security 7 đã xoá `.and()`, `WebSecurityConfigurerAdapter`).
24. Core architecture: Authentication Filter → `AuthenticationManager` (`ProviderManager`) → `AuthenticationProvider` (`DaoAuthenticationProvider`) → `UserDetailsService` + `PasswordEncoder` → `SecurityContextHolder` (ThreadLocal); authentication events; chống timing attack; ≈ Warden strategies trong Devise.
25. Authorization: URL (`authorizeHttpRequests`, quy tắc trên xuống, cụ thể trước) vs method; RBAC (`hasRole`, tự thêm `ROLE_`) vs permission (`hasAuthority`); `.roles()` và `.authorities()` ghi đè nhau.
26. Method security: `@EnableMethodSecurity(securedEnabled, jsr250Enabled)`, `@PreAuthorize` (tham số, bean `@leavePerm...`), `@PostAuthorize` (`returnObject`, method đã chạy rồi mới chặn), `@Secured("ROLE_X")`, `@RolesAllowed("X")`; ≈ Pundit.

### Transaction
27. Programmatic (`TransactionTemplate`, `PlatformTransactionManager` getTransaction/commit/rollback) vs declarative (`@Transactional`); Boot tự tạo `JpaTransactionManager`.
28. Self-invocation bỏ qua proxy; cách sửa (tách bean, `TransactionTemplate`, `ObjectProvider<Self>`); package-private chạy được từ Spring 6 (private thì không).
29. Propagation: REQUIRED, REQUIRES_NEW (connection thứ 2), SUPPORTS, NOT_SUPPORTED, NEVER, MANDATORY, NESTED (savepoint). **Rails `requires_new: true` = Spring NESTED, không phải REQUIRES_NEW.** Bẫy `UnexpectedRollbackException`.
30. Isolation (Postgres mặc định READ_COMMITTED; REPEATABLE_READ dùng snapshot), timeout, readOnly → routing primary/replica (`AbstractRoutingDataSource` + `LazyConnectionDataSourceProxy`).
31. Rollback rules: unchecked → rollback, **checked → COMMIT** (khác Rails), `rollbackFor`, `noRollbackFor`, nuốt exception, `setRollbackOnly()` (≈ `raise ActiveRecord::Rollback`), nuốt exception từ repository → rollback-only.

### Thymeleaf (Lab 6: xong form, validation, fragment)
32. Giới thiệu: SSR, natural templates, `templates/` (≈ `app/views`), `static/` (≈ `public/`), `@Controller` trả tên view, `Model` (≈ biến instance), `th:text` (escape) vs `th:utext`, `th:each`, `th:if/unless`, `th:href="@{...}"`, `th:selected`, `th:classappend`, `th:remove`, `#lists`, `#numbers`; `spring.thymeleaf.cache=false` khi dev.
33. Link `@{...}`: context path tự thêm, path variable `{id}(id=...)`, tham số thừa → query string, tự encode (space → `%20`), List → lặp tham số, `~/` server-relative, `#mvc.url('EPC#detail')` (≈ named route); Thymeleaf 3.1 bỏ `#request`.
34. Form: form backing bean (POJO getter/setter), `th:object` + `th:field="*{...}"` (text/select/radio/checkbox/textarea), `th:errors`, `th:errorclass`, `#fields.hasGlobalErrors()`, `@Valid @ModelAttribute` + `BindingResult` (ngay sau), `rejectValue`/`reject`, `@ModelAttribute` trên method (≈ before_action nạp dữ liệu select), PRG + `RedirectAttributes` (flash), `typeMismatch` trong `messages.properties`, CSRF hidden `_csrf` tự chèn bởi `th:action`.
35. Validation trên form: `@NotNull`/`@NotEmpty`/`@NotBlank`, tổng hợp lỗi `#fields.hasAnyErrors()` + `#fields.detailedErrors()` (fieldName/message/global), `th:classappend` + `#fields.hasErrors('x')` tô cả khối, `th:errorclass` chỉ trên thẻ có `th:field`.
36. Fragment: `th:fragment` (có/không tham số), file `templates/fragments/common.html` (head(title), header, footer), `~{đường/dẫn :: tên}`, `this :: tên`, `th:insert` (giữ host, chèn cả fragment) vs `th:replace` (thay host) vs `th:include` (deprecated, chỉ lấy con).

---

## 6. Tiếp theo: MINI PROJECT "Employee Management System"

Phần học theo slide đã xong (tới Thymeleaf fragment). Giờ chuyển sang **project mới, thư mục mới**, KHÔNG dùng chung playground.

**Cách dùng file này ở project mới**
- Chép `CLAUDE.md` này vào gốc project mới để Claude hiểu bối cảnh; `playground-snapshot/` là **tài liệu tham khảo** (mẫu code đã chạy), không phải code của project mới.
- Khi tạo project mới thì cập nhật lại mục 2 (đường dẫn, package, tên DB) và mục 9 cho đúng project mới.

**Quy ước cho mini project (khác playground)**
- Chia package theo tầng hoặc theo feature (vd. `employee/`, `department/`, `auth/`, `common/`), **mỗi class một file** (playground gom một file chỉ để học).
- `ddl-auto: validate` + migration (Flyway) thay cho `create`; seed riêng cho profile `dev`.
- Entity không lộ ra ngoài: REST dùng DTO record, form HTML dùng form backing bean (POJO getter/setter).
- Có test (`@WebMvcTest`, `@DataJpaTest`, Testcontainers Postgres) — playground chưa có.

**Mẫu có thể lấy từ playground** (xem `playground-snapshot/`)
| Cần làm | Tham khảo |
|---|---|
| CRUD REST + ProblemDetail | mục 8, 10 (`EmployeeController`, `GlobalExceptionHandler`) |
| Form HTML new/create/edit/update, PRG, flash | mục 17 (`EmployeePageController`, `EmployeeForm`, `employees/form.html`) |
| Hiển thị lỗi validation trên form | `employees/form.html` (tổng hợp lỗi, `th:errors`, `has-error`) |
| Layout chung head/header/footer | `templates/fragments/common.html` |
| Security URL + method, CSRF tách web/api | mục 6, 11 |
| Transaction | mục 12–14 |

**Chưa học, sẽ học khi làm project (tra khi tới lab tương ứng)**
1. Layout bằng fragment có tham số (`th:replace="~{layout :: page(~{::main})}"`) hoặc `thymeleaf-layout-dialect`; i18n `#{...}` + `messages_vi.properties`.
2. Tách exception handler: advice REST (`@RestControllerAdvice(annotations = RestController.class)`) vs advice MVC trả view; trang lỗi `templates/error/404.html`, `5xx.html`. (Playground hiện: lỗi ở trang web vẫn trả JSON.)
3. Lab 7 Logging & Profiles, Lab 8 Actuator/Scheduling/Caching.
4. Lab 9: user trong DB (`AppUser` + `UserDetailsService` đọc repo), login trả **JWT** (`OncePerRequestFilter` hoặc `spring-boot-starter-oauth2-resource-server`), `AuthenticationEntryPoint` + `AccessDeniedHandler` trả ProblemDetail cho 401/403.
5. Lab 10: thống kê bằng `@Query` (`/employees/statistics`).

---

## 7. Checklist "slide sai / lỗi thời / bẫy" đã gặp (nhắc lại khi liên quan)

- `@RequestMapping` không có method = khớp **mọi** HTTP method (slide nói mặc định GET: sai).
- `@PathVariable(id = "id")` không tồn tại → `@PathVariable("id")`. Method trả `String` thiếu `return`.
- `mvcMatcher(...)`, `antMatchers` đã bị xoá → `requestMatchers("...")` (PathPattern; `/**` chỉ ở cuối).
- `hasRole("ROLE_ADMIN")` lỗi khi khởi động → `hasRole("ADMIN")`.
- Thứ tự `requestMatchers`: quy tắc theo HTTP method phải đặt **trước** quy tắc chung (`/products/**` khớp cả `/products`).
- `Propagation.PROPAGATION_REQUIRES_NEW` → `Propagation.REQUIRES_NEW`.
- `@EnableMethodSecurity`: `prePostEnabled` mặc định đã `true`. Thiếu `@EnableMethodSecurity` → `@PreAuthorize` bị bỏ qua im lặng.
- `@Transactional` "chỉ public": lỗi thời từ Spring 6 (package-private/protected chạy với CGLIB).
- `hibernate.dialect`, `driver-class-name` khai báo tay: thừa. Lỗi "Unable to determine Dialect" = **không kết nối được DB**, không phải thiếu dialect.
- `ddl-auto: update` không thêm được cột NOT NULL vào bảng có dữ liệu (chỉ WARN rồi chạy tiếp).
- Thiếu `spring-boot-starter-validation` → annotation validation bị bỏ qua im lặng.
- `@ModelAttribute` không đọc JSON (field null, không báo lỗi).
- Không trả Entity ra JSON / Model view → dùng DTO (lazy loading, đệ quy, lộ field).
- `@RestControllerAdvice` không bắt lỗi trong Security Filter (401/403 do filter → body rỗng).
- `ConstraintViolationException`, `AccessDeniedException`, `AuthenticationException` không được `ResponseEntityExceptionHandler` xử lý → phải tự viết handler, nếu không sẽ thành 500 qua catch-all.
- Catch-all `Exception.class` mà không kế thừa `ResponseEntityExceptionHandler` → 400/404/415 thành 500.
- Advice `@Order` cao chứa catch-all nuốt mọi lỗi (độ cụ thể chỉ so trong cùng một advice).
- `@PostAuthorize`: method chạy xong (có side effect) rồi mới chặn; cẩn thận null `returnObject`.
- `noRollbackFor` + exception vẫn bay lên → dữ liệu đã lưu nhưng client nhận lỗi.
- Tiền tệ dùng `BigDecimal`, không dùng `double`.
- Thymeleaf `@{/path/{x}}` KHÔNG có `(x=...)` → giữ nguyên `{x}`; cách ngắn đúng: `@{|/path/${x}|}` hoặc `@{'/path/' + ${x}}`. Path variable không encode `/` và `&`. Giá trị null → `?param=` (Rails thì bỏ hẳn). Thymeleaf 3.1 đã bỏ `#request/#session`.
- Form: thiếu `BindingResult` ngay sau `@Valid` → `MethodArgumentNotValidException` (400, không render lại form). Tên `@ModelAttribute("x")` phải khớp `th:object="${x}"` và attribute ở GET. Spring 6+ bỏ khớp dấu `/` cuối (`/users/` ≠ `/users`). Checkbox bỏ tick không được gửi → `th:field` sinh hidden `_field`. `Map.of()` không giữ thứ tự.
- `th:errorclass` trên `<div>` (không có `th:field`/`name`) → TemplateProcessingException (slide nói "hoặc phần tử con gần": sai). `th:errors` không tự sinh `<span>`, nó thay nội dung thẻ chứa nó và xoá thẻ khi không lỗi → bọc `th:if` là thừa. `@NotEmpty` cho lọt `"   "` → dùng `@NotBlank`.
- Sửa template trong `src/` khi app đang chạy bằng `spring-boot:run` không có hiệu lực ngay: app đọc `target/classes` → `./mvnw resources:resources` (hoặc IDE build) rồi F5.
- Fragment khai báo tham số `footer(year)` mà gọi `:: footer` → lỗi "declares parameters, but fragment selection did not specify any". `th:include` đã deprecated (WARN) và bỏ thẻ gốc nên `th:text` trên thẻ gốc không chạy. Thiếu `~{...}` → WARN "Deprecated unwrapped fragment expression". Fragment định nghĩa trong trang thường cũng bị render → bọc `th:remove="all"`.
- JPQL `:param is null or ... like concat('%', :param, '%')` với Postgres: param null → Hibernate gửi null không kiểu, Postgres đoán `bytea` → `function lower(bytea) does not exist`. Sửa: `cast(:param as string)`.
- Validation (`@Email`) chạy TRƯỚC khi service chuẩn hoá dữ liệu → `" a@b.com"` (có dấu cách) bị 400.
- Trang lỗi chung phải là `templates/error.html` (gốc), KHÔNG phải `templates/error/error.html`. Thứ tự: `error/400.html` → `error/4xx.html` → `error.html` → Whitelabel. Curl không gửi `Accept: text/html` nên nhận JSON thay vì trang lỗi.
- Flash/session lần đầu → Tomcat nhét `;jsessionid=` vào URL redirect → `server.servlet.session.tracking-modes: cookie`.
- Boot 4: Flyway cần `spring-boot-starter-flyway` (+ `flyway-database-postgresql`); chỉ `flyway-core` thì không tự chạy. Test slice tách module: `spring-boot-starter-data-jpa-test` (`org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`), `spring-boot-starter-webmvc-test` (`org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc`). Testcontainers 2.x: artifact `testcontainers-postgresql`, class `org.testcontainers.postgresql.PostgreSQLContainer`.
- Trong `GlobalExceptionHandler` / class lồng: class lồng không `static` là inner class → bên ngoài không thấy tên.

---

## 8. Lỗi môi trường đã gặp & cách sửa nhanh

| Triệu chứng | Nguyên nhân | Sửa |
|---|---|---|
| `package org.springframework.xxx does not exist` | Thiếu starter | Thêm dependency, `./mvnw clean spring-boot:run` |
| `Failed to configure a DataSource: 'url' attribute is not specified` | Thiếu `spring.datasource.url` | Thêm vào `application.yml` |
| `ClassNotFoundException: org.postgresql.Driver` | Thiếu driver | Thêm `org.postgresql:postgresql` (runtime) |
| `Connection to localhost:5432 refused` (+ lỗi Dialect) | Container Postgres chưa chạy | `docker compose up -d` (Docker Desktop phải đang chạy) |
| `column "xxx" does not exist` | Bảng cũ, `ddl-auto: update` không thêm được cột | `ddl-auto: create` hoặc `docker compose down -v` |
| `cannot find symbol` với class tự tạo | Class bị lồng nhầm trong class khác | Đưa ra top-level |
| Mọi endpoint trả 401 | Vừa thêm starter security | Cấu hình `SecurityFilterChain` / dùng `-u user:pass` |

---

## 9. Trạng thái mini project hiện tại

**Đã xong: Lab 1–6** (13/11/2026). Chạy: `docker compose up -d && ./mvnw spring-boot:run`; test: `./mvnw test` (28 test, cần Docker cho Testcontainers).

| Lab | File | Nội dung |
|---|---|---|
| 1 | `hello/HelloController` | `GET /hello`, `GET /hello?name=X` |
| 2 | `common/UtilityService`, `config/AppConfig`, `demo/BeanDemoController` | `@Service` sinh mã EMP0001 / format tên / chuẩn hoá email; `@Bean PasswordEncoder` (chỉ `spring-security-crypto`, chưa bật Security); `/api/demo/password`, `/api/demo/format` |
| 4 | `db/migration/V1__create_department_and_employee.sql`, `V2__seed_departments.sql` | Bảng `department`, `employee` (id, code, name, email unique, department_id FK); seed IT/HR/Sales |
| 4 | `department/*` | Entity `Department`, `DepartmentRepository`, `DepartmentService` (`getEntity` cho EmployeeService), `GET/POST /api/departments` |
| 4 | `employee/Employee`, `EmployeeRepository` | `@ManyToOne(LAZY)`; `search(name, departmentId)` bằng `@Query` + JOIN FETCH (null = bỏ lọc); `findWithDepartmentById` (`@EntityGraph`) |
| 4–5 | `employee/EmployeeService`, `EmployeeController` | CRUD `/api/employees` (GET list `?name=&departmentId=`, GET id, POST 201, PUT, DELETE 204); code gán sau INSERT (dirty checking UPDATE) |
| 5 | `employee/dto/EmployeeRequest` | `@NotBlank`, `@Email`, `@Size`, `@NotNull departmentId` |
| 5 | `common/GlobalApiExceptionHandler` | `@RestControllerAdvice(annotations = RestController.class)` kế thừa `ResponseEntityExceptionHandler`: 400 lỗi field (`errors`), 400 JSON hỏng / sai kiểu, 404 `ResourceNotFoundException`, 409 `DuplicateResourceException`, 500 catch-all |
| 5 | `common/ResourceNotFoundException`, `DuplicateResourceException` | Có `@ResponseStatus` → trang web dùng `templates/error/404.html`, `templates/error.html` |
| 6 | `employee/EmployeePageController`, `EmployeeForm` | `/employees/list`, `/employees/add` (GET form, POST validate + PRG + flash, email trùng → `rejectValue`), `/employees/search?name=&departmentId=`; `@ModelAttribute("departments")` |
| 6 | `templates/fragments/layout.html`, `employees/{list,form,search}.html`, `static/css/app.css` | Fragment `head(title)`, `header`, `employeeTable(employees)`, `footer` |

**Test** (`src/test`): `TestcontainersConfiguration` (Postgres 17, `@ServiceConnection`), `EmployeeRepositoryTest` (`@DataJpaTest`), `EmployeeApiTest`, `EmployeePageTest`, `HelloAndBeanDemoTest` (`@SpringBootTest` + `@AutoConfigureMockMvc` + `@Transactional` rollback), `UtilityServiceTest` (unit).

**Lab 7 cần làm**: log (SLF4J) khi thêm/sửa/xoá nhân viên trong `EmployeeService`; tách `application-dev.yml` / `application-prod.yml` (datasource, log level), seed nhân viên mẫu cho profile `dev`.
