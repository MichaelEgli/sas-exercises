package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:test.properties")
public class HelloServiceIT {

	@Autowired
	private HelloService helloService;
	@Value("${hello.adult-age}")
	private int adultAge;

	@Test
	public void sayHelloToYoungPerson() {
		String greeting = helloService.sayHello("Mary", adultAge - 1);
		assertThat(greeting).isEqualTo("Hello Mary!");
	}

	@Test
	public void sayHelloToAdultPerson() {
		String greeting = helloService.sayHello("John", adultAge);
		assertThat(greeting).isEqualTo("Good morning, John!");
	}
}
