package com.proje.elektrikli_arac_sarj_sistemi.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations")
@Getter
@Setter
public class Location extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String ocpiLocationId; // CPO tarafındaki referans

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String city;
    private String postalCode;
    private String country;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String timeZone;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evse> evses = new ArrayList<>();
}
