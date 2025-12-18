package org.example;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class RepetitionTrigger implements Trigger {

	private final Instant start;
	private final long delay;
	private int repetitions;

	public RepetitionTrigger(Instant start, long delay, int repetitions) {
		this.start = start;
		this.delay = delay;
		this.repetitions = repetitions;
	}

	@Override
	public Instant nextExecution(TriggerContext triggerContext) {
		Instant lastCompletion = triggerContext.lastCompletion();
		if (lastCompletion == null) return start;
		Instant nextExecution = lastCompletion.plusSeconds(delay);
		return --repetitions > 0 ? nextExecution : null;
	}
}
