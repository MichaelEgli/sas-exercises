package org.example;

import org.example.booking.BookedOutException;
import org.example.booking.BookingService;
import org.example.logging.LoggingService;
import org.example.travel.TravelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
public class TravelServiceIT {

	@Autowired
	private TravelService travelService;
	@Autowired
	private BookingService bookingService;
	@Autowired
	private LoggingService loggingService;

	@Test
	public void bookTravel1() throws Exception {
		travelService.bookTravel("Paris", "Alice");
		assertThat(bookingService.getBookings("Alice")).hasSize(2);
		assertThat(loggingService.getLog("Alice")).isNotEmpty();
	}

	@Test
	public void bookTravel2() {
		assertThatExceptionOfType(BookedOutException.class)
				.isThrownBy(() -> travelService.bookTravel("London", "Bob"));
		assertThat(bookingService.getBookings("Bob")).isEmpty();
		assertThat(loggingService.getLog("Bob")).isNotEmpty();
		assertThat(loggingService.getLog("Error")).isNotEmpty();
	}

	@Test
	public void bookTravel3() {
		assertThatExceptionOfType(BookedOutException.class)
				.isThrownBy(() -> travelService.bookTravel("Rome", "Carol"));
		assertThat(bookingService.getBookings("Carol")).isEmpty();
		assertThat(loggingService.getLog("Carol")).isNotEmpty();
		assertThat(loggingService.getLog("Error")).isNotEmpty();
	}
}
