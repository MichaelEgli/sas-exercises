package ch.bfh.eglim8.spring.hello;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    private final GreetingService greetingService;
    @Value("${hello.adult-age}")
    private int adultAge;

    public HelloService(GreetingService greetingService) {
        this.greetingService = greetingService;
    };

    String sayHello(String name, int age){
        if (age > adultAge) {
            return ("Say " + greetingService.getGreeting() + " Oldie: " + name + " with age " + age);
        } else  {
            return ("Say " + greetingService.getGreeting() + " Newie: " + name + " with age " + age);
        }

    }
}
