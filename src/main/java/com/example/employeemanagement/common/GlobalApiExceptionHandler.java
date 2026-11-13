package com.example.employeemanagement.common;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// Lab 5: xử lý lỗi tập trung cho REST API, trả về ProblemDetail (RFC 9457, Content-Type application/problem+json).
// - annotations = RestController.class: chỉ áp dụng cho @RestController; trang Thymeleaf (@Controller)
//   không bị trả JSON mà đi theo trang lỗi templates/error/*.html.
// - Kế thừa ResponseEntityExceptionHandler: các lỗi chuẩn của Spring MVC (405, 415, 400...) giữ đúng mã,
//   nên catch-all Exception.class bên dưới không nuốt chúng thành 500.
@RestControllerAdvice(annotations = RestController.class)
public class GlobalApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalApiExceptionHandler.class);

    // @Valid @RequestBody sai → 400 kèm lỗi từng field: {"errors": {"email": "Email không hợp lệ"}}
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        ProblemDetail body = problem(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ");
        body.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    // JSON hỏng ({"name": ) hoặc sai kiểu ("departmentId": "abc") → 400 với thông báo dễ hiểu
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.badRequest().body(problem(HttpStatus.BAD_REQUEST,
            "Request body sai định dạng JSON hoặc sai kiểu dữ liệu"));
    }

    // /api/employees/abc (id phải là số) → 400
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String name = ex.getPropertyName() != null ? ex.getPropertyName() : "tham số";
        return ResponseEntity.badRequest().body(problem(HttpStatus.BAD_REQUEST,
            "Giá trị '" + ex.getValue() + "' của '" + name + "' sai kiểu dữ liệu"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    ProblemDetail handleDuplicate(DuplicateResourceException ex) {
        return problem(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Lỗi không lường trước → 500, log đầy đủ stack trace nhưng không lộ chi tiết ra client
    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception ex) {
        log.error("Lỗi không xử lý được", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra, vui lòng thử lại sau");
    }

    private static ProblemDetail problem(HttpStatus status, String detail) {
        return ProblemDetail.forStatusAndDetail(status, detail);
    }
}
