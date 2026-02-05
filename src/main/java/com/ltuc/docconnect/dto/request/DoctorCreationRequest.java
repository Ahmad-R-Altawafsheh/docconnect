package com.ltuc.docconnect.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class DoctorCreationRequest {

    @NotBlank(message = "Full name is required.")
    String fullName;

    @Size(max = 2000, message = "Bio cannot exceed 2000 characters.")
    String bio;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please provide a valid email address.")
    String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    String password;

    @NotEmpty(message = "At least one specialty must be specified.")
    Set<Long> specialtyIds;
}