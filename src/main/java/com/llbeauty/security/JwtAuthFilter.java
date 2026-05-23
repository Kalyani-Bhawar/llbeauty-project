package com.llbeauty.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT Filter — runs once per request.
 * Reads JWT from HttpOnly cookie named "LLB_TOKEN",
 * validates it and sets authentication in Spring Security context.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractTokenFromCookie(request);

        if (token != null && jwtUtil.validateToken(token)) {
            String subject = jwtUtil.extractSubject(token);
            String role    = jwtUtil.extractRole(token);

            // Set authentication in Security Context
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    subject, null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    // Read JWT from Cookie named "LLB_TOKEN"
    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("LLB_TOKEN".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
