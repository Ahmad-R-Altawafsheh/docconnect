package com.ltuc.docconnect.security.util;

import com.ltuc.docconnect.security.entities.Role;
import com.ltuc.docconnect.security.entities.User;
import com.ltuc.docconnect.security.repositories.RoleRepository;
import com.ltuc.docconnect.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

    }
}
