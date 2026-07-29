package com.proje.elektrikli_arac_sarj_sistemi.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // API için CSRF koruması şimdilik kapalı (JWT/token bazlı auth kurulunca gözden geçirilecek)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // ŞİMDİLİK herkese açık — gerçek auth sistemi kurulunca değişecek
            );
        return http.build();
    }
}