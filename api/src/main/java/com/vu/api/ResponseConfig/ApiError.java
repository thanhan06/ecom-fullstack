package com.vu.api.ResponseConfig;

import java.time.Instant;

/**
 * Class ApiError để đại diện cho lỗi trả về từ API,
 * bao gồm các trường như timestamp (thời gian lỗi xảy ra), status (mã trạng thái HTTP),
 * code (mã lỗi cụ thể), message (thông điệp lỗi) và path (đường dẫn của yêu cầu
 */
public record ApiError(Instant timestamp, int status, String code, String message, String path) {}
