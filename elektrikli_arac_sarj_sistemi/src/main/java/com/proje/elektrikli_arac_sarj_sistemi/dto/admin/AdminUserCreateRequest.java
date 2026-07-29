package com.proje.elektrikli_arac_sarj_sistemi.dto.admin;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.AdminRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminUserCreateRequest {

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    @Size(min = 3, max = 50, message = "Kullanıcı adı 3-50 karakter arasında olmalı")
    private String username;

    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 8, message = "Şifre en az 8 karakter olmalı")
    private String password; // dikkat: hash değil, ham şifre — hashleme service'te yapılacak

    private AdminRole role;

    // getter/setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public AdminRole getRole() { return role; }
    public void setRole(AdminRole role) { this.role = role; }
}
