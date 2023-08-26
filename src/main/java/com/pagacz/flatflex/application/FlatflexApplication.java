package com.pagacz.flatflex.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(value = {"com.pagacz.*"})
@EnableScheduling
@EnableJpaRepositories(basePackages = {"com.pagacz.*"})
@EntityScan("com.pagacz.flatflex.domain.model")
public class FlatflexApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlatflexApplication.class, args);
	}

}
