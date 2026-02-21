package com.day.track;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DaytrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(DaytrackApplication.class, args);
	}
}
