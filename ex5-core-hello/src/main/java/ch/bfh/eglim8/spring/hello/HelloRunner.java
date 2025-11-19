package ch.bfh.eglim8.spring.hello;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class HelloRunner implements CommandLineRunner {

    private final HelloService helloService;

    public HelloRunner(HelloService helloService) {
        this.helloService = helloService;
    }

    public void run(String... args) throws Exception {
        System.out.println(helloService.sayHello("Michael", 47));

        //GreetingService greetingService = new GreetingService();
        //HelloService helloService = new HelloService(greetingService);

    }

}
