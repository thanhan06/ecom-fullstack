package com.vu.api.ResponseConfig;

import java.time.Instant;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vu.api.ErrorConfig.ApiException;
import com.vu.api.ErrorConfig.ErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Xử lý các lỗi Custom Exception (Business Logic) của bạn
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException ex, HttpServletRequest req) {
        ErrorCode ec = ex.getErrorCode();
        return ResponseEntity.status(ec.status())
                .body(new ApiError(Instant.now(), ec.status().value(), ec.code(), ec.message(), req.getRequestURI()));
    }

    // 2. Bổ sung: Xử lý lỗi Validation (Ví dụ: Password < 8 ký tự)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        // Lấy câu thông báo lỗi mặc định mà bạn đã define trong DTO (VD: "Password must be at least 8 characters long")
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErrorCode ec = ErrorCode.valueOf(errorMessage); // Chuyển message thành ErrorCode enum
        // Trả về HTTP Status 400 (Bad Request) cùng với format ApiError của bạn
        // Lưu ý: Chỗ "VALIDATION_ERROR" bạn có thể đổi thành một Enum ErrorCode nếu muốn đồng bộ hoàn toàn
        return ResponseEntity.status(ec.status())
                .body(new ApiError(Instant.now(), ec.status().value(), ec.code(), ec.message(), req.getRequestURI()));
    }
    // ... các @ExceptionHandler khác của bạn (ví dụ bắt ApiException, MethodArgumentNotValidException)

    /**
     * Bắt lỗi khi người dùng không có quyền truy cập (Lỗi phân quyền từ @PreAuthorize)
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiError> handlingAccessDeniedException(
            AccessDeniedException exception, HttpServletRequest req) {

        // Sử dụng ErrorCode.USER_NOT_AUTHORIZED mà bạn đã định nghĩa
        ErrorCode ec = ErrorCode.USER_NOT_AUTHORIZED;

        // Trả về ApiResponse chứa mã lỗi và HTTP Status 403 FORBIDDEN
        return ResponseEntity.status(ec.status())
                .body(new ApiError(Instant.now(), ec.status().value(), ec.code(), ec.message(), req.getRequestURI()));
    }
}
