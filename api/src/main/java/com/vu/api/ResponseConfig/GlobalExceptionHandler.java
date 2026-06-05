package com.vu.api.ResponseConfig;

import java.time.Instant;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vu.api.ErrorConfig.ApiException;
import com.vu.api.ErrorConfig.ErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleApi(ApiException ex, HttpServletRequest req) {
        ErrorCode ec = ex.getErrorCode();
        return ResponseEntity.status(ec.status())
                .body(new ApiError(Instant.now(), ec.status().value(), ec.code(), ec.message(), req.getRequestURI()));
    }
}
