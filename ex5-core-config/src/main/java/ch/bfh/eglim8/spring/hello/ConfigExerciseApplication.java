package ch.bfh.eglim8.spring.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class ConfigExerciseApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigExerciseApplication.class, args);

        ApplicationContext context = new AnnotationConfigApplicationContext(RunnerConfig.class);

        // Getting the bean
        HelloService helloService = context.getBean("configHelloService", HelloService.class);

        // Invoking the method inside main() method
        helloService.sayHello("Michael", 77);
	}

}
