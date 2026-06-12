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

/**
 * Class GlobalExceptionHandler để xử lý các lỗi toàn cục trong ứng dụng của bạn,
 * bao gồm cả lỗi tùy chỉnh (ApiException) và lỗi validation (MethodArgumentNotValidException).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bắt lỗi tùy chỉnh ApiException và trả về phản hồi lỗi chuẩn với mã lỗi, thông điệp và HTTP Status tương ứng.
     * @param ex ApiException chứa thông tin về lỗi đã xảy ra.
     * @param req HttpServletRequest để lấy thông tin về đường dẫn yêu cầu (path).
     * @return ResponseEntity chứa ApiError với thông tin lỗi chuẩn.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException ex, HttpServletRequest req) {
        ErrorCode ec = ex.getErrorCode();
        return ResponseEntity.status(ec.status())
                .body(new ApiError(Instant.now(), ec.status().value(), ec.code(), ec.message(), req.getRequestURI()));
    }

    /**
     * Bắt lỗi validation MethodArgumentNotValidException và trả về phản hồi lỗi chuẩn với mã lỗi,
     * thông điệp và HTTP Status tương ứng.
     * @param ex MethodArgumentNotValidException chứa thông tin về lỗi validation đã xảy ra
     * @param req HttpServletRequest để lấy thông tin về đường dẫn yêu cầu (path).
     * @return ResponseEntity chứa ApiError với thông tin lỗi chuẩn.
     */
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

    /**
     * Bắt lỗi AccessDeniedException khi người dùng không có quyền truy cập vào tài nguyên và trả về phản hồi lỗi chuẩn với mã lỗi,
     * thông điệp và HTTP Status tương ứng.
     * @param exception AccessDeniedException chứa thông tin về lỗi truy cập đã xảy ra
     * @param req HttpServletRequest để lấy thông tin về đường dẫn yêu cầu (path).
     * @return ResponseEntity chứa ApiError với thông tin lỗi chuẩn và HTTP Status 403 Forbidden.
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
