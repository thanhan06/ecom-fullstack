package com.vu.api.ErrorConfig;

import java.util.IllegalFormatException;

import org.springframework.http.HttpStatus;

/**
 * Enum ErrorCode để định nghĩa các mã lỗi cụ thể cho API,
 * bao gồm code (mã lỗi), status (trạng thái HTTP) và message (thông điệp lỗi) cho từng loại lỗi.
 * Các mã lỗi bao gồm lỗi liên quan đến người dùng (USER_NOT_FOUND, USER_EXIST, INVALID_PASSWORD, v.v.),
 * lỗi liên quan đến token (REFRESH_TOKEN_NOT_FOUND, INVALID_ACCESS_TOKEN, v.v.) và lỗi máy chủ (INTERNAL_SERVER_ERROR).
 */
public enum ErrorCode {
    // User related error codes
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "User not found"),
    USER_EXIST("USER_EXIST", HttpStatus.CONFLICT, "User already exist"),
    INVALID_PASSWORD("INVALID_PASSWORD", HttpStatus.UNAUTHORIZED, "Invalid password"),
    USER_NOT_AUTHENTICATED("USER_NOT_AUTHENTICATED", HttpStatus.UNAUTHORIZED, "User not authenticated"),
    USER_NOT_AUTHORIZED("USER_NOT_AUTHORIZED", HttpStatus.FORBIDDEN, "User not authorized to access this resource"),
    USERNAME_INVALID(
            "USERNAME_INVALID", HttpStatus.BAD_REQUEST, "Username must be between {min} and {max} characters long"),
    USERNAME_BLANK("USERNAME_BLANK", HttpStatus.BAD_REQUEST, "Username cannot be blank"),
    USERID_BLANK("USERID_BLANK", HttpStatus.BAD_REQUEST, "User ID cannot be blank"),

    // Token related error codes
    REFRESH_TOKEN_NOT_FOUND("REFRESH_TOKEN_NOT_FOUND", HttpStatus.NOT_FOUND, "Refresh token not found"),
    PASSWORD_TOO_WEAK("PASSWORD_TOO_WEAK", HttpStatus.BAD_REQUEST, "Password must be at least {min} characters long"),
    INVALID_ACCESS_TOKEN("INVALID_ACCESS_TOKEN", HttpStatus.UNAUTHORIZED, "Invalid access token"),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", HttpStatus.UNAUTHORIZED, "Invalid refresh token"),
    REFRESH_TOKEN_NOT_BLANK("REFRESH_TOKEN_NOT_BLANK", HttpStatus.BAD_REQUEST, "Refresh token cannot be blank"),

    // General error codes
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"),

    // Product type related error codes
    PRODUCT_TYPE_NAME_IS_NOT_BLANK(
            "PRODUCT_TYPE_NAME_IS_NOT_BLANK", HttpStatus.BAD_REQUEST, "Product type name cannot be blank"),
    PRODUCT_TYPE_NAME_IS_EXIST("PRODUCT_TYPE_NAME_IS_EXIST", HttpStatus.CONFLICT, "Product type name already exist"),
    PRODUCT_TYPE_NOT_FOUND("PRODUCT_TYPE_NOT_FOUND", HttpStatus.NOT_FOUND, "Product type not found");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }

    /**
     * Phương thức formatMessage để định dạng thông điệp lỗi với các tham số động nếu cần thiết.
     * Ví dụ, nếu message của ErrorCode là "Username must be between %d and %d characters long", bạn có thể gọi formatMessage(3, 10) để nhận được thông điệp đã được định dạng là "Username must be between 3 and 10 characters long".
     * @param args các tham số động để định dạng thông điệp lỗi
     * @return thông điệp lỗi đã được định dạng với các tham số đã cung cấp, hoặc message gốc nếu có lỗi trong quá trình định dạng (ví dụ: sai số lượng hoặc kiểu tham số)
     * @throws IllegalFormatException nếu có lỗi trong quá trình định dạng thông điệp (ví dụ: sai số lượng hoặc kiểu tham số)
     */
    public String formatMessage(Object... args) {
        try {
            return String.format(this.message, args);
        } catch (Exception e) {
            // Trường hợp truyền sai số lượng hoặc kiểu tham số, trả về message gốc để tránh crash
            return this.message;
        }
    }
}
