package com.proje.elektrikli_arac_sarj_sistemi.mapper;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.AdminUser;
import com.proje.elektrikli_arac_sarj_sistemi.dto.admin.AdminUserCreateRequest;
import com.proje.elektrikli_arac_sarj_sistemi.dto.admin.AdminUserResponse;
import org.springframework.stereotype.Component;

@Component
public class AdminUserMapper {

    public AdminUser toEntity(AdminUserCreateRequest request) {

        AdminUser adminUser = new AdminUser();

        adminUser.setUsername(request.getUsername());
        adminUser.setRole(request.getRole());
        adminUser.setActive(true);

        return adminUser;
    }

    public AdminUserResponse toResponse(AdminUser adminUser) {

        return new AdminUserResponse(
                adminUser.getId(),
                adminUser.getUsername(),
                adminUser.getRole(),
                adminUser.isActive(),
                adminUser.getLastLoginAt()
        );
    }
}