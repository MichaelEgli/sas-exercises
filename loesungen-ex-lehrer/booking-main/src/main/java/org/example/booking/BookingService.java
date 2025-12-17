package org.example.booking;

import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.booking.Booking.Service.FLIGHT;
import static org.example.booking.Booking.Service.HOTEL;

@Service
public class BookingService {

	private final BookingRepository bookingRepository;

	public BookingService(BookingRepository bookingRepository) {
		this.bookingRepository = bookingRepository;
	}

	public void bookFlight(String destination, String customer) throws BookedOutException {
		if (destination.length() < 5)
			throw new BookedOutException("Flights to " + destination + " booked out");
		bookingRepository.save(new Booking(FLIGHT, destination, customer));
	}

	public void bookHotel(String destination, String customer) throws BookedOutException {
		if (destination.length() > 5)
			throw new BookedOutException("Hotels in " + destination + " booked out");
		bookingRepository.save(new Booking(HOTEL, destination, customer));
	}

	public List<Booking> getBookings(String customer) {
		return bookingRepository.findByCustomer(customer);
	}
}
