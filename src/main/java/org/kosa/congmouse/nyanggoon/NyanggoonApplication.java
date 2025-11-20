package org.kosa.congmouse.nyanggoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NyanggoonApplication {

	public static void main(String[] args) {
		SpringApplication.run(NyanggoonApplication.class, args);
	}

}
