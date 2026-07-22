package com.llbeauty.config;

import com.llbeauty.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // 🔐 Password encoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

        // 🚨 DISABLE CSRF (IMPORTANT for Postman + APIs)
        .csrf(csrf -> csrf.disable())

        // 🚀 JWT = STATELESS SYSTEM
        .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        // 🔐 AUTH RULES
        .authorizeHttpRequests(auth -> auth
        		
                // 🟢 PUBLIC AUTH APIs (MOST IMPORTANT)
                .requestMatchers("/auth/**").permitAll()

                // 🟢 PUBLIC PAGES / RESOURCES
                .requestMatchers(
                        "/",
                        "/shop",
                        "/products",
                        "/product/**",
                        "/store/**",
                        "/membership/**",
                        "/member/verify/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**",
                        "/about",
                        "/contact",
                        "/franchise",
                        "/salon",
                        "/wallet/**",
                        "/razorpay/**",
                        "/api/nxl/**",
                        "/checkout/**",
                        "/matrimony/**"
                ).permitAll()

                // 🔴 ADMIN ONLY
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // 🔒 EVERYTHING ELSE REQUIRES LOGIN
                .anyRequest().authenticated()
        )

        // 🔥 JWT FILTER
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}