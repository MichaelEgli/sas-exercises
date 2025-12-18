package org.example;

import org.example.booking.BookingService;
import org.example.logging.LoggingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

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
	public void bookTravel1() throws Exception {
		jmsTemplate.convertAndSend(queue, "Paris:Alice");
		Thread.sleep(1000);
		assertThat(bookingService.getBookings("Alice")).hasSize(2);
		assertThat(loggingService.getLog("Alice")).isNotEmpty();
	}

	@Test
	public void bookTravel2() throws Exception {
		jmsTemplate.convertAndSend(queue, "London:Bob");
		Thread.sleep(1000);
		assertThat(bookingService.getBookings("Bob")).isEmpty();
		assertThat(loggingService.getLog("Bob")).isNotEmpty();
		assertThat(loggingService.getLog("Error")).isNotEmpty();
	}

	@Test
	public void bookTravel3() throws Exception {
		jmsTemplate.convertAndSend(queue, "Rome:Carol");
		Thread.sleep(1000);
		assertThat(bookingService.getBookings("Carol")).isEmpty();
		assertThat(loggingService.getLog("Carol")).isNotEmpty();
		assertThat(loggingService.getLog("Error")).isNotEmpty();
	}
}
