package com.ltuc.docconnect.util;

public interface SecurityConstants {

    String ADMIN_ONLY = "hasRole('ADMIN')";
    String PATIENT_OR_ADMIN = "hasAnyRole('PATIENT', 'ADMIN')";
    String DOCTOR_OR_ADMIN = "hasAnyRole('DOCTOR', 'ADMIN')";
    String ANY_USER = "isAuthenticated()";
}