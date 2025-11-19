package ch.bfh.eglim8.spring.hello;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GreetingService {

    public LocalDateTime getGreeting() {
        return LocalDateTime.now();
    }
}
