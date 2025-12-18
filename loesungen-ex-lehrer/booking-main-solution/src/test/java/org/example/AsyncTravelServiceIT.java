package org.example;

import org.awaitility.Awaitility;
import org.example.booking.BookingService;
import org.example.logging.LoggingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AsyncTravelServiceIT {

	@Autowired
	private JmsTemplate jmsTemplate;
	@Value("${booking.queue}")
	private String queue;
	@Autowired
	private BookingService bookingService;
	@Autowired
	private LoggingService loggingService;

	@Test
	public void bookTravel1() {
		jmsTemplate.convertAndSend(queue, "Alice:Paris");
		Awaitility.await().atMost(10, SECONDS).untilAsserted(() -> {
			assertThat(bookingService.getBookings("Alice")).hasSize(2);
			assertThat(loggingService.getLog("Alice")).isNotEmpty();
		});
	}

	@Test
	public void bookTravel2() {
		jmsTemplate.convertAndSend(queue, "Bob:London");
		Awaitility.await().atMost(10, SECONDS).untilAsserted(() -> {
			assertThat(bookingService.getBookings("Bob")).isEmpty();
			assertThat(loggingService.getLog("Bob")).isNotEmpty();
			assertThat(loggingService.getLog("Error")).isNotEmpty();
		});
	}

	@Test
	public void bookTravel3() {
		jmsTemplate.convertAndSend(queue, "Carol:Rome");
		Awaitility.await().atMost(10, SECONDS).untilAsserted(() -> {
			assertThat(bookingService.getBookings("Carol")).isEmpty();
			assertThat(loggingService.getLog("Carol")).isNotEmpty();
			assertThat(loggingService.getLog("Error")).isNotEmpty();
		});
	}
}
