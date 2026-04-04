package com.syncride.userservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (Common cause for 403 in microservices)
                .csrf(csrf -> csrf.disable())

                // 2. Set session management to stateless (since we use JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/verify-otp", "/api/create-user", "/api/update-otp", "/api/get-user",
                                "/api/get-user-fcm", "/api/update-history", "/api/remove-ride-history",
                                "/api/update-cancelled-ride", "/api/update-complete-ride", "/api/update-leave-ride",
                                "/api/get-drivers", "/api/get-specific-drivers", "/api/set-driver-status",
                                "/api/update-request-complete-ride", "/api/update-request-confirmed-ride",
                                "/api/update-request-cancel-ride", "/api/update-ride-history",
                                "/api/update-schedule-ride-history", "/api/upload-documents", "/api/ping")
                        .permitAll() // Public
                        .requestMatchers("/api/logout", "/api/get-user-history", "/api/update-location",
                                "/api/get-profile")
                        .hasAnyRole("DRIVER", "USER")
                        .requestMatchers("/api/create-admin")
                        .hasRole("SUPER_ADMIN")
                        .requestMatchers("/api/create-superadmin", "/api/get-superadmins",
                                "/api/get-admins", "/api/get-admins-by-org")
                        .hasRole("SYSTEM_ADMIN")
                        .anyRequest().authenticated())

                // 4. Add your custom filter before the standard auth filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}