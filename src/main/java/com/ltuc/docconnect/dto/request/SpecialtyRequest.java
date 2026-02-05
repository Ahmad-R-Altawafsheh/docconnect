package com.ltuc.docconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SpecialtyRequest {

    @NotBlank(message = "Specialty name cannot be blank.")
    @Size(max = 100)
    String name;

}