package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class HelloServiceTest {

	@Autowired
	private HelloService helloService;
	@MockitoBean
	private GreetingService greetingService;
	@Value("${hello.adult-age}")
	private int adultAge;

	@BeforeEach
	public void setupMock() {
		Mockito.when(greetingService.getGreeting()).thenReturn("Good afternoon");
	}

	@Test
	public void sayHelloToYoungPerson() {
		String greeting = helloService.sayHello("Mary", adultAge - 1);
		assertThat(greeting).isEqualTo("Hello Mary!");
	}

	@Test
	public void sayHelloToAdultPerson() {
		String greeting = helloService.sayHello("John", adultAge);
		assertThat(greeting).isEqualTo("Good afternoon, John!");
	}
}
