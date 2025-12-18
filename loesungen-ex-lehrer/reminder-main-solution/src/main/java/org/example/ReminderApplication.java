package org.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Scanner;

@SpringBootApplication
@EnableScheduling
public class ReminderApplication implements CommandLineRunner {

	private final ReminderService reminderService;

	public static void main(String[] args) {
		SpringApplication.run(ReminderApplication.class, args);
	}

	public ReminderApplication(ReminderService reminderService) {
		this.reminderService = reminderService;
	}

	@Override
	public void run(String[] args) {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.print("Reminder Time: ");
			LocalTime time = LocalTime.parse(scanner.nextLine());
			Instant instant = ZonedDateTime.of(LocalDate.now(), time, ZoneId.systemDefault()).toInstant();
			System.out.print("Reminder Text: ");
			String text = scanner.nextLine();
			reminderService.scheduleReminder(instant, text);
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
		}
	}
}
