package org.example.logging;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
public class LoggingService {

	private final LogEntryRepository logEntryRepository;

    public LoggingService(LogEntryRepository logEntryRepository) {
		this.logEntryRepository = logEntryRepository;
	}

    @Transactional(propagation = REQUIRES_NEW)
	public void log(String message) {
		logEntryRepository.save(new LogEntry(message));
	}

	public List<LogEntry> getLog(String keyword) {
		return logEntryRepository.findByKeyword(keyword);
	}
}
