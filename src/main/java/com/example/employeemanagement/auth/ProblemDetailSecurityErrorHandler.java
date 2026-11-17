package com.example.employeemanagement.auth;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

// Lab 9: lỗi 401/403 xảy ra trong SECURITY FILTER (trước DispatcherServlet) nên @RestControllerAdvice
// không bắt được → body rỗng. Class này tự ghi ProblemDetail JSON giống GlobalApiExceptionHandler.
// - AuthenticationEntryPoint: chưa đăng nhập / token sai, hết hạn → 401
// - AccessDeniedHandler: đã đăng nhập nhưng thiếu quyền (USER gọi POST) → 403
@Component
public class ProblemDetailSecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public ProblemDetailSecurityErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        String detail = ex instanceof InvalidBearerTokenException
            ? "Token không hợp lệ hoặc đã hết hạn"
            : "Cần đăng nhập (Authorization: Bearer <token>)";
        // Chuẩn RFC 6750: báo client kiểu xác thực cần dùng
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        write(request, response, HttpStatus.UNAUTHORIZED, detail);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        write(request, response, HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện thao tác này");
    }

    private void write(HttpServletRequest request, HttpServletResponse response,
                       HttpStatus status, String detail) throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "about:blank");
        body.put("title", status.getReasonPhrase());
        body.put("status", status.value());
        body.put("detail", detail);
        body.put("instance", request.getRequestURI());
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
