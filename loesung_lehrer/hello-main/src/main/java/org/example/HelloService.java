package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class HelloService {

	private final GreetingService greetingService;
	@Value("${hello.adult-age}")
	private int adultAge;

	public HelloService(GreetingService greetingService) {
		this.greetingService = greetingService;
	}

	public String sayHello(String name, int age) {
		if (age < adultAge)
			return "Hello " + name + "!";
		else return greetingService.getGreeting() + ", " + name + "!";
	}

	@PostConstruct
	public void init() {
		System.out.println("--- HelloService: init");
	}

	@PreDestroy
	public void cleanup() {
		System.out.println("--- HelloService: cleanup");
	}
}
