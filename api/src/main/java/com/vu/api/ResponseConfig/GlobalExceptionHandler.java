package com.vu.api.ResponseConfig;

import java.time.Instant;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;

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
                .body(new ApiError(
                        Instant.now(), ec.status().value(), ec.code(), ex.getMessage(), req.getRequestURI()));
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

        String errorMessageKey = ex.getBindingResult().getFieldError().getDefaultMessage();
        ErrorCode ec;
        String finalMessage;

        try {
            ec = ErrorCode.valueOf(errorMessageKey);
            finalMessage = ec.message(); // Lấy chuỗi gốc: "Username must be between {min} and {max}..."

            // Mở gói lỗi để lấy Map chứa toàn bộ các tham số của Annotation (min, max, value...)
            var constraintViolation = ex.getBindingResult().getFieldError().unwrap(ConstraintViolation.class);
            Map<String, Object> attributes =
                    constraintViolation.getConstraintDescriptor().getAttributes();

            // VÒNG LẶP THẦN THÁNH: Tự động dò tìm và thay thế mọi placeholder
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}"; // Tạo chuỗi dạng {min}, {max}

                // Nếu trong câu thông báo có chứa {min} hoặc {max}, tự động thay bằng số thực tế
                if (finalMessage.contains(placeholder)) {
                    finalMessage = finalMessage.replace(placeholder, String.valueOf(entry.getValue()));
                }
            }
        } catch (Exception e) {
            ec = ErrorCode.INTERNAL_SERVER_ERROR;
            finalMessage = errorMessageKey != null ? errorMessageKey : "Validation error";
        }

        return ResponseEntity.status(ec.status())
                .body(new ApiError(Instant.now(), ec.status().value(), ec.code(), finalMessage, req.getRequestURI()));
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
