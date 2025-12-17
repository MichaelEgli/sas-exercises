package org.example.client;

import java.time.LocalDate;

public class Todo {

	private Long id;
	private String title;
	private LocalDate dueDate;
	private boolean completed;
	private int version;

	public Todo() {
	}

	public Todo(String title, LocalDate dueDate) {
		this.title = title;
		this.dueDate = dueDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Todo " + id + ": " + title + (dueDate != null ? ", due on " + dueDate : "") + " - " +
				(completed ? "completed" : "pending");
	}
}
