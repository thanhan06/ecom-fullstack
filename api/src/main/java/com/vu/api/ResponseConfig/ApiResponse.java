package com.vu.api.ResponseConfig;

import java.time.Instant;

public record ApiResponse<T>(Instant timestamp, int status, String message, String path, T data) {}
