package com.vu.api.ResponseConfig;

import java.time.Instant;

/**
 * Class ApiResponse để đại diện cho phản hồi trả về từ API,
 * bao gồm các trường như timestamp (thời gian phản hồi), status (mã trạng thái HTTP),
 * message (thông điệp phản hồi), path (đường dẫn của yêu cầu) và data (dữ liệu trả về).
 */
public record ApiResponse<T>(Instant timestamp, int status, String message, String path, T data) {}
