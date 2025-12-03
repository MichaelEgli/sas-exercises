package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ChatListener {

	@Value("${chat.user}")
	private String user;

	@JmsListener(destination = "${chat.topic}")
	public void onMessage(ChatMessage chatMessage) {
		String sender = chatMessage.user();
		String text = chatMessage.text();
		if (sender != null && !sender.equals(user) && text != null) {
			System.out.println(chatMessage);
		}
	}
}
