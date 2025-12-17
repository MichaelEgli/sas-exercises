package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.springframework.boot.WebApplicationType.NONE;

@SpringBootApplication
public class RestClient {

	public static void main(String[] args) {
        new SpringApplicationBuilder(RestClient.class).web(NONE).run(args);
	}
}
