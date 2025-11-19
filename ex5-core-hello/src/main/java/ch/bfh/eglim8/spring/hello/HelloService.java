package ch.bfh.eglim8.spring.hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    private final GreetingService greetingService;

    public HelloService(GreetingService greetingService) {
        this.greetingService = greetingService;
    };

    String sayHello(String name, int age){
        greetingService.getGreeting();
        return ("Say Hello: " + name + " with age " + age);
    }
}
