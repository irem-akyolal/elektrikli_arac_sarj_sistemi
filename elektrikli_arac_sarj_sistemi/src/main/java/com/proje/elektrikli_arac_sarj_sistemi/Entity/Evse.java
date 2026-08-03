package com.proje.elektrikli_arac_sarj_sistemi.Entity;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EvseStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evses")
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL") 
public class Evse extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String ocpiEvseUid; // OCPI'deki uid alanı

    private String evseId; // OCPI'nin insan-okunur evse_id alanı 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvseStatus status;

    @OneToMany(mappedBy = "evse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Connector> connectors = new ArrayList<>();
}