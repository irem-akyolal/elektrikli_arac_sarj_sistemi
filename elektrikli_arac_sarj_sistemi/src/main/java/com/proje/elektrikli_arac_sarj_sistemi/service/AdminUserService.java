package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.AdminUser;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.AuditAction;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.AdminUserRepository;
import com.proje.elektrikli_arac_sarj_sistemi.audit.Auditable;
import com.proje.elektrikli_arac_sarj_sistemi.dto.admin.AdminUserCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.admin.AdminUserResponse;
import com.proje.elektrikli_arac_sarj_sistemi.exception.BusinessRuleViolationException;
import com.proje.elektrikli_arac_sarj_sistemi.exception.ResourceNotFoundException;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.AdminUserMapper;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminUserMapper adminUserMapper;

    public AdminUserService(AdminUserRepository adminUserRepository,
                            PasswordEncoder passwordEncoder,
                            AdminUserMapper adminUserMapper) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUserMapper = adminUserMapper;
    }

    
    @PreAuthorize("hasRole('SUPER_ADMIN')")
     @Auditable(action = AuditAction.CREATE, entityType = "ADMIN_USER")
    @Transactional
    public AdminUserResponse create(AdminUserCreateRequest request) {
        validateUsername(request.getUsername());

        AdminUser adminUser = adminUserMapper.toEntity(request);
        adminUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        AdminUser savedAdmin = adminUserRepository.save(adminUser);
        return adminUserMapper.toResponse(savedAdmin);
    }


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public AdminUserResponse getById(UUID id) {
        AdminUser adminUser = findAdminUser(id);
        return adminUserMapper.toResponse(adminUser);
    }


    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<AdminUserResponse> getAll() {
        return adminUserRepository.findAll()
                .stream()
                .map(adminUserMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
     @Auditable(action = AuditAction.DEACTIVATE, entityType = "ADMIN_USER")
    @Transactional
    public void deactivate(UUID id) {
        AdminUser adminUser = findAdminUser(id);
        adminUser.setActive(false);
        adminUserRepository.save(adminUser); 
    }

    // ============================
    // Private Methods
    // ============================

    private void validateUsername(String username) {
        if (adminUserRepository.existsByUsername(username)) {
            throw new BusinessRuleViolationException(
                    "USERNAME_ALREADY_EXISTS",
                    "Bu kullanıcı adı zaten kullanılıyor : " + username
            );
        }
    }

    private AdminUser findAdminUser(UUID id) {
        return adminUserRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Admin kullanıcı bulunamadı : " + id
                        ));
    }
}