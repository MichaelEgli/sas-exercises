package ch.bfh.eglim8.spring.hello;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Scanner;

@Configuration
public class RunnerConfig {

    private final HelloService helloService;

    public RunnerConfig(HelloService helloService) {
        this.helloService = helloService;
    }

    @Bean
    @Profile("dev")
    public CommandLineRunner testRunner(HelloService helloService) {
        return args -> {
            System.out.println(helloService.sayHello("Roman", 79));
            System.out.println(helloService.sayHello("Michael", 81));
        };
    }

    @Bean
    @Profile("prod")
    public CommandLineRunner helloRunner(HelloService helloService) {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Your name: ");
            String name = scanner.nextLine();
            System.out.print("Your age: ");
            int age = Integer.parseInt(scanner.nextLine());
            String message = helloService.sayHello(name, age);
            System.out.println(message);
        };
    }

}
