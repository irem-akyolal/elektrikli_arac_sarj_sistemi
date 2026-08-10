package com.proje.elektrikli_arac_sarj_sistemi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;




@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableResilientMethods
public class ElektrikliAracSarjSistemiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElektrikliAracSarjSistemiApplication.class, args);
	}

}
