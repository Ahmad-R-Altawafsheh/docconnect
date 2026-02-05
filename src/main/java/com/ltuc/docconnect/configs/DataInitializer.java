package com.ltuc.docconnect.configs;

import com.ltuc.docconnect.dto.request.AvailabilityRequest;
import com.ltuc.docconnect.dto.request.DoctorCreationRequest;
import com.ltuc.docconnect.dto.request.ReportCreationRequest;
import com.ltuc.docconnect.dto.request.SpecialtyRequest;
import com.ltuc.docconnect.entity.*;
import com.ltuc.docconnect.enums.AppointmentStatus;
import com.ltuc.docconnect.enums.Specialities;
import com.ltuc.docconnect.repository.*;
import com.ltuc.docconnect.security.entities.Role;
import com.ltuc.docconnect.security.entities.User;
import com.ltuc.docconnect.security.repositories.RoleRepository;
import com.ltuc.docconnect.security.repositories.UserRepository;
import com.ltuc.docconnect.service.interfaces.command.AdminCommandService;
import com.ltuc.docconnect.service.interfaces.command.DoctorCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final SpecialtyRepository specialtyRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    private final AdminCommandService adminCommandService;
    private final DoctorCommandService doctorCommandService;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepo.count() > 0) return;

        Map<String, Role> roles = initRoles();
        initAdminAccount(roles);

        fakeLogin("admin@docconnect.com", "ROLE_ADMIN");
        initSpecialtiesFromEnum();
        List<Long> doctorIds = seedTwentyDoctorsViaAdmin();

        List<Patient> patients = initOneHundredPatients(roles.get("ROLE_PATIENT"));

        seedAvailabilitiesViaDoctors(doctorIds);
        seedAppointmentsAndReports(doctorIds, patients);

        SecurityContextHolder.clearContext();
    }

    private Map<String, Role> initRoles() {
        Map<String, Role> roles = new HashMap<>();
        roles.put("ROLE_ADMIN", roleRepo.findByName("ROLE_ADMIN").orElseGet(() -> roleRepo.save(new Role(null, "ROLE_ADMIN"))));
        roles.put("ROLE_DOCTOR", roleRepo.findByName("ROLE_DOCTOR").orElseGet(() -> roleRepo.save(new Role(null, "ROLE_DOCTOR"))));
        roles.put("ROLE_PATIENT", roleRepo.findByName("ROLE_PATIENT").orElseGet(() -> roleRepo.save(new Role(null, "ROLE_PATIENT"))));
        return roles;
    }

    private void initAdminAccount(Map<String, Role> roles) {
        User user = new User();
        user.setUsername("admin@docconnect.com");
        user.setEmail("admin@docconnect.com");
        user.setPassword(passwordEncoder.encode("admin123"));
        user.setRoles(new HashSet<>(Collections.singletonList(roles.get("ROLE_ADMIN"))));
        userRepo.save(user);
    }

    private void initSpecialtiesFromEnum() {
        for (Specialities spec : Specialities.values()) {
            SpecialtyRequest request = SpecialtyRequest.builder()
                    .name(spec.name().replace("_", " "))
                    .build();
            adminCommandService.createSpecialty(request);
        }
    }

    private List<Long> seedTwentyDoctorsViaAdmin() {
        List<Long> doctorIds = new ArrayList<>();
        List<Specialty> savedSpecialties = specialtyRepository.findAll();

        for (int i = 1; i <= 20; i++) {
            Set<Long> specIds = Collections.singleton(savedSpecialties.get(i % savedSpecialties.size()).getId());

            DoctorCreationRequest request = DoctorCreationRequest.builder()
                    .fullName("Dr. Doctor " + i)
                    .email("doctor" + i + "@docconnect.com")
                    .password("doctor123")
                    .bio("Expert in medical services.")
                    .specialtyIds(specIds)
                    .build();

            var response = adminCommandService.createDoctor(request);
            doctorIds.add(response.getId());
        }
        return doctorIds;
    }

    private List<Patient> initOneHundredPatients(Role patientRole) {
        List<Patient> patients = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            User user = new User();
            String email = "patient" + i + "@gmail.com";
            user.setUsername(email);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("patient123"));
            user.setRoles(new HashSet<>(Set.of(patientRole)));
            userRepo.save(user);

            Patient patient = new Patient();
            patient.setUser(user);
            patient.setFullName("Patient " + i);
            patient.setPhone("079000000" + i);
            patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
            patients.add(patientRepository.save(patient));
        }
        return patients;
    }

    private void seedAvailabilitiesViaDoctors(List<Long> doctorIds) {
        for (int i = 0; i < doctorIds.size(); i++) {
            String email = "doctor" + (i + 1) + "@docconnect.com";
            fakeLogin(email, "ROLE_DOCTOR");

            AvailabilityRequest request = AvailabilityRequest.builder()
                    .dayOfWeek(DayOfWeek.values()[i % 7])
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(17, 0))
                    .build();

            doctorCommandService.addAvailability(request, SecurityContextHolder.getContext().getAuthentication(), null);
        }
    }

    private void seedAppointmentsAndReports(List<Long> doctorIds, List<Patient> patients) {
        for (int i = 0; i < 5; i++) {
            Appointment app = new Appointment();
            app.setDoctor(doctorRepository.findById(doctorIds.get(i)).orElse(null));
            app.setPatient(patients.get(i));
            app.setDate(LocalDate.now().minusDays(1));
            app.setStartTime(LocalTime.of(10, 0));
            app.setEndTime(LocalTime.of(10, 30));
            app.setStatus(AppointmentStatus.BOOKED);
            app.setFakePaymentTransactionId(UUID.randomUUID().toString());
            app = appointmentRepository.save(app);

            fakeLogin("doctor" + (i + 1) + "@docconnect.com", "ROLE_DOCTOR");

            ReportCreationRequest reportRequest = ReportCreationRequest.builder()
                    .diagnosis("Diagnosis for patient " + (i + 1))
                    .treatment("Treatment plan alpha")
                    .notes("Observe for 48 hours")
                    .build();

            doctorCommandService.createReport(app.getId(), reportRequest, SecurityContextHolder.getContext().getAuthentication());
        }
    }

    private void fakeLogin(String email, String role) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                email, null, List.of(() -> role));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}