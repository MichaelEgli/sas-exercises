package org.example.service;

public class TodoNotFoundException extends Exception {

	public TodoNotFoundException(String message) {
		super(message);
	}
}
