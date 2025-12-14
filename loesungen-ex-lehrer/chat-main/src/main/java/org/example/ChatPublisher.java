package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ChatPublisher implements CommandLineRunner {

	@Value("${chat.topic}")
	private String topic;
	@Value("${chat.user}")
	private String user;

	private final JmsTemplate jmsTemplate;

	public ChatPublisher(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	@Override
	public void run(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter messages to sent...");
		while (true) {
			String text = scanner.nextLine();
			if (text.isEmpty()) break;
			ChatMessage chatMessage = new ChatMessage(user, text);
			jmsTemplate.convertAndSend(topic, chatMessage);
		}
	}
}
