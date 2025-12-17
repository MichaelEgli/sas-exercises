package org.example.travel;

import org.example.booking.BookedOutException;
import org.example.booking.BookingService;
import org.example.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TravelService {

    @Autowired
    public final BookingService bookingService;

    @Autowired
    public final LoggingService loggingService;

    public TravelService(BookingService bookingService, LoggingService loggingService) {
        this.bookingService = bookingService;
        this.loggingService = loggingService;
    }

    @Transactional(rollbackFor = BookedOutException.class)
	public void bookTravel(String destination, String customer) throws BookedOutException {
        try {
            bookingService.bookFlight(destination, customer);
            bookingService.bookHotel(destination, customer);
            loggingService.log("Booked travel for " + customer + " to " + destination);
        } catch (BookedOutException e) {
            loggingService.log(e.toString());
            throw new BookedOutException(e.toString());
        }
	}
}
