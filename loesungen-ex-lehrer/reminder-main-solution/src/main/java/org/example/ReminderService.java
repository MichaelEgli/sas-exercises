package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;

@Service
public class ReminderService {

	private final ThreadPoolTaskScheduler taskScheduler;

	@Value("${reminder.repetitions}")
	private int repetitions;
	@Value("${reminder.delay}")
	private long delay;

	public ReminderService(ThreadPoolTaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}

	public void scheduleReminder(Instant start, String text) {
		taskScheduler.schedule(
				() -> System.out.println("Reminder: " + text + " (" + LocalTime.now().withNano(0) + ")"),
				new RepetitionTrigger(start, delay, repetitions)
		);
	}
}
