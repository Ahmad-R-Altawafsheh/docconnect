package com.ltuc.docconnect.service.interfaces.command;

import com.ltuc.docconnect.dto.request.DoctorCreationRequest;
import com.ltuc.docconnect.dto.request.SpecialtyRequest;
import com.ltuc.docconnect.dto.response.DoctorCreationResponse;
import com.ltuc.docconnect.dto.response.SpecialtyResponse;

public interface AdminCommandService {

    SpecialtyResponse createSpecialty(SpecialtyRequest request);

    DoctorCreationResponse createDoctor(DoctorCreationRequest request);
}