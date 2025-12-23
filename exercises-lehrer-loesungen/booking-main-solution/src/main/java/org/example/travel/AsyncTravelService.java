package org.example.travel;

import org.example.booking.BookedOutException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class AsyncTravelService {

	private final TravelService travelService;

	public AsyncTravelService(TravelService travelService) {
		this.travelService = travelService;
	}

	@JmsListener(destination = "${booking.queue}")
	public void onMessage(String message) {
		String[] tokens = message.split(":");
		try {
			travelService.bookTravel(tokens[0], tokens[1]);
		} catch (BookedOutException ex) {
			System.out.println("--- Error: " + ex);
		}
	}
}
