package com.llbeauty.security;

import com.llbeauty.entity.User;
import com.llbeauty.repository.UserRepository;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Updated JwtAuthFilter — production-ready
 * - Reads JWT from HttpOnly cookie "LLB_TOKEN"
 * - Validates JWT
 * - Sets authentication in Spring Security context
 * - Handles blocked users and clears cookie if blocked
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

            boolean isBlocked = false;
            if ("USER".equals(role)) {
                try {
                    WebApplicationContext ctx = WebApplicationContextUtils
                        .getRequiredWebApplicationContext(request.getServletContext());
                    UserRepository userRepository = ctx.getBean(UserRepository.class);
                    Optional<User> userOpt = userRepository.findByEmail(subject);
                    if (userOpt.isPresent() && Boolean.TRUE.equals(userOpt.get().getIsBlocked())) {
                        isBlocked = true;
                    }
                } catch (Exception e) {
                    // Safe fallback: ignore context errors
                }
            }

            if (isBlocked) {
                SecurityContextHolder.clearContext();
                // Clear cookie for blocked user
                Cookie cookie = new Cookie("LLB_TOKEN", null);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
                response.sendRedirect("/auth/login?error=Your account has been blocked by the Administrator");
                return;
            }

            // Set authentication in Security Context for Spring Security
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    subject, null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    // Extract JWT from HttpOnly cookie named "LLB_TOKEN"
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