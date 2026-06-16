package com.vu.api.ErrorConfig;

/**
 * Class ApiException để đại diện cho các lỗi xảy ra trong API,
 * bao gồm một ErrorCode để xác định loại lỗi cụ thể.
 */
public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;

    // Constructor 1: Dành cho các lỗi cơ bản không có tham số động (VD: USER_NOT_FOUND)
    public ApiException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    // Constructor 2 (MỚI): Dành cho các lỗi có tham số động (VD: USERNAME_INVALID cần min, max)
    public ApiException(ErrorCode errorCode, Object... args) {
        // Gọi hàm formatMessage từ Enum ErrorCode để ghép số vào chuỗi thông báo
        super(errorCode.formatMessage(args));
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
