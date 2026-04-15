package com.vu.api.user.DTO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.vu.api.validator.DobConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record UserUpdateRequest(
        String email,
        String password,
        @JsonProperty("dob") @JsonFormat(pattern = "yyyy-MM-dd") @DobConstraint(min = 18, message = "INVALID_DOB") LocalDate dob,
        List<String> roleNames
) {
}
