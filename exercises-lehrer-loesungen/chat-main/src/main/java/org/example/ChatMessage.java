package org.example;

public record ChatMessage(String user, String text) {

	@Override
	public String toString() {
		return "Message from " + user + ": " + text;
	}
}
