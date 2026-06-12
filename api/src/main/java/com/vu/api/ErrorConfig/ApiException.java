package com.vu.api.ErrorConfig;

/**
 * Class ApiException để đại diện cho các lỗi xảy ra trong API,
 * bao gồm một ErrorCode để xác định loại lỗi cụ thể.
 */
public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
