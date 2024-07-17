package de.hawhh.knap.bpm_rpa;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BpmRpaApplication {

	public static void main(String[] args) {

		SpringApplication.run(BpmRpaApplication.class, args);

		LoggerFactory.getLogger(BpmRpaApplication.class)
				.info("Application is up and running. Waiting for active processes.");
		System.out.println("BPM-RPA Application is ready to go");
	}

}
