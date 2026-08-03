package com.proje.elektrikli_arac_sarj_sistemi.dto.admin;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.AdminRole;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdminUserResponse {

    private UUID id;
    private String username;
    private AdminRole role;
    private boolean active;
    private LocalDateTime lastLoginAt;

    public AdminUserResponse(UUID id, String username, AdminRole role, boolean active, LocalDateTime lastLoginAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.active = active;
        this.lastLoginAt = lastLoginAt;
    }

   
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public AdminRole getRole() { return role; }
    public boolean isActive() { return active; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
}