package com.proje.elektrikli_arac_sarj_sistemi.Config;

import com.proje.elektrikli_arac_sarj_sistemi.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public — kimlik doğrulama gerektirmeyen endpoint'ler
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/locations/active", "/api/locations/active/**").permitAll()
                 .requestMatchers("/api/charging-sessions/start",
                 "/api/charging-sessions/*/charging",
                 "/api/charging-sessions/*/complete",
                 "/api/charging-sessions/*/connector-removed").permitAll()
                 .requestMatchers("/api/charging-sessions/*").permitAll() 
                .requestMatchers("/api/provisions/**").permitAll()
                .requestMatchers("/api/payments/**").permitAll()
                .requestMatchers("/api/invoices/**").permitAll()
                // Admin — token gerektiren endpoint'ler
                .requestMatchers("/api/admin-users/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/api/admin/**").hasAnyRole("SUPER_ADMIN", "OPERATOR", "VIEWER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}