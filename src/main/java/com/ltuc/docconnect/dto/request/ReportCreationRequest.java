package com.ltuc.docconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ReportCreationRequest {

    @NotBlank(message = "Diagnosis is required.")
    @Size(max = 1000, message = "Diagnosis cannot exceed 1000 characters.")
    String diagnosis;

    @NotBlank(message = "Treatment is required.")
    @Size(max = 1000, message = "Treatment cannot exceed 1000 characters.")
    String treatment;

    @Size(max = 2000, message = "Notes cannot exceed 2000 characters.")
    String notes;
}