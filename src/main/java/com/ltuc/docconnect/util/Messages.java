package com.ltuc.docconnect.util;

public final class Messages {

    private Messages() {
    }

    public static final String SPECIALTY_CREATED_SUCCESS = "Specialty created successfully.";
    public static final String SPECIALTIES_RESPONSE_SUCCESS = "Specialties retrieved successfully.";
    public static final String DOCTOR_REGISTERED_SUCCESS = "Doctor registered successfully.";
    public static final String DOCTOR_LIST_RETRIEVED = "Doctor list retrieved successfully.";
    public static final String APPOINTMENT_LIST_RETRIEVED_FOR_DATE = "Doctor's appointments retrieved successfully for %s.";
    public static final String REPORT_CREATED_SUCCESS = "Consultation report created and appointment status updated successfully.";
    public static final String AVAILABILITY_UPDATED_SUCCESS = "Availability updated successfully.";
    public static final String APPOINTMENT_BOOKED_SUCCESS = "Appointment booked successfully. Transaction reference: %s";
    public static final String PATIENT_HISTORY_RETRIEVED_SUCCESS = "Patient appointment history retrieved successfully.";
    public static final String APPOINTMENT_CANCELLED_SUCCESS = "Appointment cancelled successfully.";
    public static final String AVAILABLE_SLOTS_RETRIEVED_FOR_DATE = "Available time slots for the selected date have been retrieved successfully: %s.";
    public static final String RESOURCE_NOT_FOUND_TEMPLATE = "%s not found with %s: '%s'";
    public static final String INPUT_VALIDATION_FAILED = "Input validation failed.";
    public static final String TYPE_MISMATCH_TEMPLATE = "Argument type error: value '%s' is invalid for field '%s'. Required type: %s.";
    public static final String DATA_INTEGRITY_VIOLATION = "Data integrity violation: unique constraint violated (possibly duplicate name or double booking).";
    public static final String UNEXPECTED_SERVER_ERROR = "Unexpected server error: %s";
    public static final String DATE_IN_PAST = "Appointment date cannot be in the past.";
    public static final String APPOINTMENT_TIME_IN_PAST = "The requested time has already passed for today.";
    public static final String DOCTOR_NOT_AVAILABLE_ON_DAY = "The doctor does not have working hours on: %s.";
    public static final String SLOT_OUTSIDE_WORKING_HOURS = "The requested time %s is outside the doctor's working hours.";
    public static final String SLOT_ALREADY_BOOKED = "The requested time %s is already booked by another patient.";
    public static final String SLOT_NOT_AVAILABLE = "The requested slot %s is currently unavailable.";
    public static final String CANNOT_CANCEL_NOT_BOOKED = "Only appointments with 'BOOKED' status can be cancelled.";
    public static final String CANNOT_REPORT_INVALID_STATUS = "Cannot create report for an appointment that is not BOOKED or COMPLETED.";
    public static final String DOCTOR_UNAUTHORIZED_REPORT = "Doctor is not authorized to create a report for this appointment.";
    public static final String NOT_AUTHORIZED_APPOINTMENT_OWNERSHIP = "You are not authorized to access or modify this appointment.";
    public static final String INVALID_AVAILABILITY_TIME = "Start time must be before end time, and the duration must be at least 30 minutes.";    public static final String DOCTOR_ID_REQUIRED_FOR_ADMIN = "Admin must provide a doctorId for this operation.";
    public static final String REPORT_ALREADY_EXISTS = "A medical report already exists for this appointment.";
    public static final String DUPLICATE_AVAILABILITY = "You already have a scheduled availability for this day. Only one period per day is allowed.";
    public static final String PATIENT_ID_REQUIRED_FOR_ADMIN = "Admin must provide a patientId for this operation.";
    public static final String SPECIALTY_ALREADY_EXISTS = "Specialty '%s' already exists.";
    public static final String DOCTOR_MIN_SPECIALTY_REQUIRED = "At least one specialty must be assigned to the doctor.";
    public static final String INVALID_SPECIALTY_IDS = "One or more specialty IDs were not found.";
    public static final String EMAIL_ALREADY_REGISTERED = "This email is already registered.";
    public static final String ROLE_NOT_FOUND = "Role 'ROLE_DOCTOR' not found in the system.";
    public static final String CANNOT_REPORT_BEFORE_TIME = "Cannot create a report before the appointment's scheduled start time.";
}