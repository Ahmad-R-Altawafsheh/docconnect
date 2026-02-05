package com.ltuc.docconnect.controllers;

import com.ltuc.docconnect.security.dtos.AuthRequest;
import com.ltuc.docconnect.security.dtos.AuthResponse;
import com.ltuc.docconnect.security.services.JwtService;
import com.ltuc.docconnect.security.services.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final MyUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );
        UserDetails ud = userDetailsService.loadUserByUsername(req.email());
        String token = jwtService.generateToken(ud.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
    }

}