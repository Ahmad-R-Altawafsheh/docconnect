package com.ltuc.docconnect.service.impl.command;

import com.ltuc.docconnect.dto.request.DoctorCreationRequest;
import com.ltuc.docconnect.dto.request.SpecialtyRequest;
import com.ltuc.docconnect.dto.response.DoctorCreationResponse;
import com.ltuc.docconnect.dto.response.SpecialtyResponse;
import com.ltuc.docconnect.entity.Doctor;
import com.ltuc.docconnect.entity.Specialty;
import com.ltuc.docconnect.exception.BadRequestException;
import com.ltuc.docconnect.exception.ResourceNotFoundException;
import com.ltuc.docconnect.mapper.SpecialtyMapper;
import com.ltuc.docconnect.repository.DoctorRepository;
import com.ltuc.docconnect.repository.SpecialtyRepository;
import com.ltuc.docconnect.security.entities.Role;
import com.ltuc.docconnect.security.entities.User;
import com.ltuc.docconnect.security.repositories.RoleRepository;
import com.ltuc.docconnect.security.repositories.UserRepository;
import com.ltuc.docconnect.service.interfaces.command.AdminCommandService;
import com.ltuc.docconnect.util.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
class AdminCommandServiceImpl implements AdminCommandService {

    private final SpecialtyRepository specialtyRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final SpecialtyMapper specialtyMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public SpecialtyResponse createSpecialty(SpecialtyRequest request) {
        String normalizedName = request.getName().trim().toUpperCase();

        if (specialtyRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new BadRequestException(String.format(Messages.SPECIALTY_ALREADY_EXISTS, normalizedName));
        }

        Specialty specialty = new Specialty();
        specialty.setName(normalizedName);
        return specialtyMapper.toResponse(specialtyRepository.save(specialty));
    }

    @Override
    public DoctorCreationResponse createDoctor(DoctorCreationRequest request) {
        if (request.getSpecialtyIds() == null || request.getSpecialtyIds().isEmpty()) {
            throw new BadRequestException(Messages.DOCTOR_MIN_SPECIALTY_REQUIRED);
        }

        List<Specialty> specialties = specialtyRepository.findAllById(request.getSpecialtyIds());
        if (specialties.size() != request.getSpecialtyIds().size()) {
            throw new ResourceNotFoundException("Specialty", "ids", Messages.INVALID_SPECIALTY_IDS);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(Messages.EMAIL_ALREADY_REGISTERED);
        }

        User user = createDoctorUser(request);

        Doctor doctor = new Doctor();
        doctor.setFullName(request.getFullName());
        doctor.setBio(request.getBio());
        doctor.setUser(user);
        user.setDoctor(doctor);

        specialties.forEach(doctor::addSpecialty);

        Doctor savedDoctor = doctorRepository.save(doctor);

        return DoctorCreationResponse.builder()
                .id(savedDoctor.getId())
                .fullName(savedDoctor.getFullName())
                .bio(savedDoctor.getBio())
                .build();
    }

    private User createDoctorUser(DoctorCreationRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role doctorRole = roleRepository.findByName("ROLE_DOCTOR")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", Messages.ROLE_NOT_FOUND));
        user.getRoles().add(doctorRole);
        return user;
    }
}