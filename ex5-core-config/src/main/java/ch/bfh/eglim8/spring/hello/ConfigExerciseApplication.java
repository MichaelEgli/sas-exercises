package ch.bfh.eglim8.spring.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ConfigExerciseApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigExerciseApplication.class, args);
	}

}
