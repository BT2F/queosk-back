package com.bttf.queosk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class QueoskApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueoskApplication.class, args);
	}

}
