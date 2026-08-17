package com.proje.elektrikli_arac_sarj_sistemi.Config;

import com.proje.elektrikli_arac_sarj_sistemi.security.CustomAccessDeniedHandler;
import com.proje.elektrikli_arac_sarj_sistemi.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;


    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:5173"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
   }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http 
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public — kimlik doğrulama gerektirmeyen endpoint'ler
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/locations/active", "/api/locations/active/**").permitAll()
                .requestMatchers("/api/public/locations/nearby").permitAll()
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

             .exceptionHandling(exception ->
                exception.accessDeniedHandler(customAccessDeniedHandler)
            )
            
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

          

        return http.build();
    }
}