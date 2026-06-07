package com.vu.api.ErrorConfig;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "User not found"),
    USER_EXIST("USER_EXIST", HttpStatus.CONFLICT, "User already exist"),
    INVALID_PASSWORD("INVALID_PASSWORD", HttpStatus.UNAUTHORIZED, "Invalid password"),
    USER_NOT_AUTHENTICATED("USER_NOT_AUTHENTICATED", HttpStatus.UNAUTHORIZED, "User not authenticated");
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
}
