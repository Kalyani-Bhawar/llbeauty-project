package com.llbeauty.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Utility — generates and validates JWT tokens.
 * Secret key is loaded from application.properties.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs; // default 24 hours

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ---- Generate Token ----
    public String generateToken(String subject, String role) {
        return Jwts.builder()
                .subject(subject)                          // mobile or email
                .claim("role", role)                      // USER or ADMIN
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // ---- Extract Mobile / Email from Token ----
    public String extractSubject(String token) {
        return parseClaims(token).getPayload().getSubject();
    }

    // ---- Extract Role ----
    public String extractRole(String token) {
        return (String) parseClaims(token).getPayload().get("role");
    }

    // ---- Validate Token ----
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ---- Parse Claims (private helper) ----
    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
    }
}
