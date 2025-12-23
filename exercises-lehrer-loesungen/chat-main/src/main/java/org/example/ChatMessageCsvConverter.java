package org.example;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

public class ChatMessageCsvConverter implements MessageConverter {

	private static final String CSV_DELIMITER = ",";

	@Override
	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		if (object instanceof ChatMessage(String user, String text)) {
			String content = user + CSV_DELIMITER + text;
			return session.createTextMessage(content);
		} else throw new MessageConversionException("Invalid message type: " + object.getClass().getName());
	}

	@Override
	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
		if (message instanceof TextMessage textMessage) {
			String content = textMessage.getText();
			String[] tokens = content.split(CSV_DELIMITER);
			if (tokens.length < 2) {
				throw new MessageConversionException("Invalid message format: " + content);
			}
			return new ChatMessage(tokens[0], tokens[1]);
		} else throw new MessageConversionException("Invalid message type: " + message.getClass().getName());
	}
}
