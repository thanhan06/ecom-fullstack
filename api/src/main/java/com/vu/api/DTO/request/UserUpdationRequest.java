package com.vu.api.DTO.request;

import jakarta.validation.constraints.Size;

import org.openapitools.jackson.nullable.JsonNullable;

public record UserUpdationRequest(
        @Size(min = 3, max = 10, message = "Username must be between 3 and 10 characters long")
                JsonNullable<String> username,
        @Size(min = 8, message = "PASSWORD_TOO_WEAK") JsonNullable<String> password,
        JsonNullable<String> role) {}
