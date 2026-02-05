package com.ltuc.docconnect.security.services;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private Key key;

    @Value("${jwt.base64-secret:}")
    private String base64Secret;

    @Value("${jwt.access-token-ttl-ms:3600000}")
    private long ttlMs;

    @PostConstruct
    public void init() {
        if (base64Secret == null || base64Secret.isBlank()) {
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            byte[] secretBytes = Base64.getDecoder().decode(base64Secret);
            this.key = Keys.hmacShaKeyFor(secretBytes);
        }
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttlMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String username) {
        try {
            String sub = extractUsername(token);
            return (sub.equals(username) && !isExpired(token));
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    private boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
