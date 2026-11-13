package com.example.employeemanagement.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Lab 5: vi phạm ràng buộc unique (email nhân viên, tên phòng ban) → 409 Conflict.
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
