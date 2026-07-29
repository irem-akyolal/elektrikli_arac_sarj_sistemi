package com.proje.elektrikli_arac_sarj_sistemi.Entity;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.AdminRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_users")
@Getter
@Setter
@NoArgsConstructor 
@SQLRestriction("deleted_at IS NULL")
public class AdminUser extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminRole role;

    @Column(nullable = false)
    private boolean active = true;

    private LocalDateTime lastLoginAt;
}