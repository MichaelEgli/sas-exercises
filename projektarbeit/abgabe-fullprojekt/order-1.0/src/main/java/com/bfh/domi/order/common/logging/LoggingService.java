package com.bfh.domi.order.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);

    // Here you would typically save the log to a database or external system
	//
    // @Transactional(propagation = Propagation.REQUIRES_NEW)
	public void log(String message) {
		        logger.info("Logging message: {}", message);
	}
}
