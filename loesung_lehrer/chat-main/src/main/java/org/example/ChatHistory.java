package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;

@Component
public class ChatHistory {

	@Value("${chat.user}")
	private String user;
	@Value("${chat.history.topic}")
	private String topic;
	@Value("${chat.history.timeout}")
	private long timeout;

	private final JmsTemplate jmsTemplate;
	private List<ChatMessage> messages = new ArrayList<>();

	public ChatHistory(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	@PostConstruct
	public void getHistory() {
		jmsTemplate.setReceiveTimeout(timeout);
		Message reply = jmsTemplate.sendAndReceive(topic, session -> {
			Message message = session.createTextMessage();
			message.setStringProperty("user", user);
			return message;
		});
		if (reply == null) return;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String data = ((TextMessage) reply).getText();
			messages = objectMapper.readValue(data, new TypeReference<>() {});
			messages.forEach(System.out::println);
		} catch (JMSException | JacksonException ex) {
			System.out.println("--- Error: " + ex.getMessage());
		}
	}

	@JmsListener(destination = "${chat.history.topic}", selector = "user <> '${chat.user}'")
	public void onRequest(Message request) {
		try {
			jmsTemplate.convertAndSend(request.getJMSReplyTo(), messages);
		} catch (JMSException ex) {
			System.out.println("--- Error: " + ex.getMessage());
		}
	}

	@JmsListener(destination = "${chat.topic}")
	public void onMessage(ChatMessage chatMessage) {
		String sender = chatMessage.user();
		String text = chatMessage.text();
		if (sender != null && text != null) {
			messages.add(chatMessage);
		}
	}
}
