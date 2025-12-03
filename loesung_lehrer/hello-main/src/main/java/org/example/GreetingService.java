package org.example;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.LocalTime;

@Service
public class GreetingService {

	private final TimesOfDay timesOfDay;

	public GreetingService(TimesOfDay timesOfDay) {
		this.timesOfDay = timesOfDay;
	}

	public String getGreeting() {
		return "Good " + getTimeOfDay();
	}

	private String getTimeOfDay() {
		int hour = LocalTime.now().getHour();
		if (hour < timesOfDay.morning()) return "night";
		else if (hour < timesOfDay.afternoon()) return "morning";
		else if (hour < timesOfDay.evening()) return "afternoon";
		else return "evening";
	}

	@PostConstruct
	public void init() {
		System.out.println("--- GreetingService: init");
	}

	@PreDestroy
	public void cleanup() {
		System.out.println("--- GreetingService: cleanup");
	}
}
