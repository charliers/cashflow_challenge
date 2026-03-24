package com.ciandt.challenge.command;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
@ComponentScan(basePackages = {"com.ciandt.challenge.command"})
public class CommandCashflowServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommandCashflowServiceApplication.class, args);
	}

}
