package com.vu.api.user.DTO.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vu.api.validator.DobConstraint;

public record UserUpdateRequest(
        String email,
        String password,
        @JsonProperty("dob") @JsonFormat(pattern = "yyyy-MM-dd") @DobConstraint(min = 18, message = "INVALID_DOB")
                LocalDate dob,
        List<String> roleNames) {}
