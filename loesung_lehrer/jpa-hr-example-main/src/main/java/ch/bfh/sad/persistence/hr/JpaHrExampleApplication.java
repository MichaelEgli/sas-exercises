package ch.bfh.sad.persistence.hr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class JpaHrExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaHrExampleApplication.class, args);
    }

}
