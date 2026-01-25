package com.example.hospitalAppointmentSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HospitalAppointmentSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitalAppointmentSystemApplication.class, args);
	}

}
