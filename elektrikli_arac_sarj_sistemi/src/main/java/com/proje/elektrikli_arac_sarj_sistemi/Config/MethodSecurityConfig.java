package com.proje.elektrikli_arac_sarj_sistemi.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity //  @PreAuthorize yetkilendirmesi yapabilmek için gerekli bir sınıf
public class MethodSecurityConfig {
}
