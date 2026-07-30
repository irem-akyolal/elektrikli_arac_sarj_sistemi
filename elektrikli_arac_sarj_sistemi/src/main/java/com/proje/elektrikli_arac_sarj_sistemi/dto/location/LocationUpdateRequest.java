package com.proje.elektrikli_arac_sarj_sistemi.dto.location;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LocationUpdateRequest {

    @Size(min = 3, max = 50, message = "Lokasyon adı 3-50 karakter arasında olmalıdır")
    private String name;

    @Size(max = 255, message = "Adres çok uzun")
    private String address;

    @Size(max = 100, message = "Şehir adı çok uzun")
    private String city;

    @Size(max = 20, message = "Posta kodu çok uzun")
    private String postalCode;

    @Size(max = 2, message = "Ülke kodu 2 karakter olmalıdır")
    private String country;

    @DecimalMin(value = "-90.0", message = "Enlem -90 ile 90 arasında olmalıdır")
    @DecimalMax(value = "90.0", message = "Enlem -90 ile 90 arasında olmalıdır")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Boylam -180 ile 180 arasında olmalıdır")
    @DecimalMax(value = "180.0", message = "Boylam -180 ile 180 arasında olmalıdır")
    private Double longitude;

    @Size(max = 50, message = "Zaman dilimi geçersiz")
    private String timeZone;
}
