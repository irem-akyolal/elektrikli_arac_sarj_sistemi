package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.AdminUser;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.AuditAction;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.AdminUserRepository;
import com.proje.elektrikli_arac_sarj_sistemi.audit.Auditable;
import com.proje.elektrikli_arac_sarj_sistemi.dto.auth.LoginRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.auth.LoginResponse;
import com.proje.elektrikli_arac_sarj_sistemi.exception.BusinessRuleViolationException;
import com.proje.elektrikli_arac_sarj_sistemi.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AdminUserRepository adminUserRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Auditable(action = AuditAction.LOGIN, entityType = "ADMIN_USER")
    @Transactional
    public LoginResponse login(LoginRequest request) {
        AdminUser adminUser = adminUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "INVALID_CREDENTIALS", "Kullanıcı adı veya şifre hatalı"));

        if (!adminUser.isActive()) {
            throw new BusinessRuleViolationException(
                    "ACCOUNT_INACTIVE", "Bu hesap devre dışı bırakılmış");
        }

        if (!passwordEncoder.matches(request.getPassword(), adminUser.getPasswordHash())) {
            throw new BusinessRuleViolationException(
                    "INVALID_CREDENTIALS", "Kullanıcı adı veya şifre hatalı");
        }

        adminUser.setLastLoginAt(LocalDateTime.now());
        adminUserRepository.save(adminUser);

        String token = jwtService.generateToken(adminUser.getUsername(), adminUser.getRole().name());
        return new LoginResponse(token, adminUser.getUsername(), adminUser.getRole().name());
    }
}