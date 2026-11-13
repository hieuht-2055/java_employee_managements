package com.example.employeemanagement.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Lab 5: không tìm thấy bản ghi (≈ ActiveRecord::RecordNotFound → 404).
// - REST: GlobalApiExceptionHandler bắt và trả ProblemDetail.
// - Trang Thymeleaf: advice REST không áp dụng → @ResponseStatus cho Spring đặt 404
//   rồi Boot render templates/error/404.html.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " với id " + id + " không tồn tại");
    }
}
