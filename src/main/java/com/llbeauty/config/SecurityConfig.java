package com.llbeauty.config;

import com.llbeauty.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for simplified cookie/OTP authentication
            .authorizeHttpRequests(auth -> auth
                // Allow public access to home page, static resources, and basic pages
                .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/about", "/contact", "/franchise", "/salon", "/auth/**", "/franchise/apply", "/salon/book").permitAll()
                // Admin access only
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // All other requests require login (like /shop, /cart, /checkout)
                .anyRequest().authenticated()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("LLB_TOKEN")
                .permitAll()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
