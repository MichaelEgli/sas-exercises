package ch.bfh.eglim8.spring.hello;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RunnerConfig {

    @Bean
    public HelloService configHelloService() {
        return new HelloService(new GreetingService());
    }



/*    @Bean(name ="runner")
    public CommandLineRunner runner() {
        return args -> {
            new CommandLineRunner() {
                public void run(String... args) {
                    System.out.println(helloService.sayHello("Michael", 81));
                }
            };
        };
    }*/




/*    private final HelloService helloService;

    public RunnerConfig(HelloService helloService) {
        this.helloService = helloService;
    }

    public void run(String... args) throws Exception {
        System.out.println(helloService.sayHello("Michael", 81));

    }*/
}
