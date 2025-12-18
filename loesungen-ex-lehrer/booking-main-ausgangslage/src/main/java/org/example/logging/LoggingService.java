package org.example.logging;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoggingService {

	private final LogEntryRepository logEntryRepository;

	public LoggingService(LogEntryRepository logEntryRepository) {
		this.logEntryRepository = logEntryRepository;
	}

	public void log(String message) {
		logEntryRepository.save(new LogEntry(message));
	}

	public List<LogEntry> getLog(String keyword) {
		return logEntryRepository.findByKeyword(keyword);
	}
}
