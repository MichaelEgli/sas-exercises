package org.example.travel;

import org.example.booking.BookedOutException;
import org.example.booking.BookingService;
import org.example.logging.LoggingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TravelService {

	private final BookingService bookingService;
	private final LoggingService loggingService;

	public TravelService(BookingService bookingService, LoggingService loggingService) {
		this.bookingService = bookingService;
		this.loggingService = loggingService;
	}

	@Transactional(rollbackFor = BookedOutException.class)
	public void bookTravel(String customer, String destination) throws BookedOutException {
		loggingService.log("Book travel to " + destination + " for " + customer);
		try {
			bookingService.bookFlight(customer, destination);
			bookingService.bookHotel(customer, destination);
		} catch (BookedOutException ex) {
			loggingService.log("Error: " + ex.getMessage());
			throw ex;
		}
	}
}
