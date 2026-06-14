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

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
        .csrf(csrf -> csrf
        	    .ignoringRequestMatchers(
        	        "/razorpay/webhook",
        	        "/manual-payment/submit",
        	        "/checkout/confirm-order",
        	        "/checkout/place-order",
        	        "/checkout/cart/**",

        	        "/membership/buy",
        	        "/membership/confirm",

        	        "/store/merchant/pay-initiate",
        	        "/store/executive/pay-initiate",
        	        "/store/merchant/apply-confirm",
        	        "/store/executive/apply-confirm",
        	        "/api/upload"
        	    )
        	)

            // JWT uses stateless authentication
//            .sessionManagement(session ->
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/shop",
                    "/products",
                    "/product/**",
                    "/css/**",
                    "/images/**",
                    "/js/**",
                    "/uploads/**",
                    "/about",
                    "/contact",
                    "/contact/submit",
                    "/franchise",
                    "/salon",
                    "/auth/**",
                    "/franchise/apply",
                    "/salon"
            		).permitAll()
            		// Admin access only
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // All other requests require login (like /shop, /cart, /checkout)
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
        
    }
}
