package ch.bfh.eglim8.spring.jms.jms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

@SpringBootApplication
@EnableJms
public class JmsApplication {

	public static void main(String[] args) {
        SpringApplication.run(JmsApplication.class, args);
	}

}
