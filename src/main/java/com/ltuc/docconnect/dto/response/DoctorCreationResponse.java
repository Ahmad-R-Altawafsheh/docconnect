package com.ltuc.docconnect.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorCreationResponse {

    private Long id;
    private String fullName;
    private String bio;
}