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

	public List<Booking> getBookings(String customer) {
		return bookingRepository.findByCustomer(customer);
	}

	public void bookFlight(String customer, String destination) throws BookedOutException {
		if (destination.length() < 5)
			throw new BookedOutException("Flights to " + destination + " booked out");
		bookingRepository.save(new Booking(FLIGHT, destination, customer));
	}

	public void bookHotel(String customer, String destination) throws BookedOutException {
		if (destination.length() > 5)
			throw new BookedOutException("Hotels in " + destination + " booked out");
		bookingRepository.save(new Booking(HOTEL, destination, customer));
	}
}
