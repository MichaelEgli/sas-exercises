package org.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

@Component
public class TodoClient implements CommandLineRunner {

	private final TodoService todoService;

	public TodoClient(TodoService todoService) {
		this.todoService = todoService;
	}

	@Override
	public void run(String[] args) {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println();
			System.out.println("1 List Todos");
			System.out.println("2 Add Todo");
			System.out.println("3 Complete Todo");
			System.out.println("4 Remove Todo");
			System.out.println("x Exit");
			System.out.print("> ");
			try {
				switch (scanner.nextLine()) {
					case "1" -> todoService.getTodos().forEach(this::printTodo);
					case "2" -> {
						System.out.print("Title:    ");
						String title = scanner.nextLine();
						System.out.print("Due Date: ");
						String line = scanner.nextLine();
						LocalDate dueDate = line.isEmpty() ? null : LocalDate.parse(line);
						Todo todo = todoService.addTodo(title, dueDate);
						System.out.println("Todo " + todo.getId() + " added");
					}
					case "3" -> {
						System.out.print("Id: ");
						long id = Long.parseLong(scanner.nextLine());
						Todo todo = todoService.findTodo(id);
						todo.setCompleted(true);
						todo = todoService.updateTodo(todo);
						System.out.println("Todo " + todo.getId() + " completed");
					}
					case "4" -> {
						System.out.print("Id: ");
						long id = Long.parseLong(scanner.nextLine());
						todoService.removeTodo(id);
						System.out.println("Todo " + id + " removed");
					}
					case "x" -> System.exit(0);
					default -> System.out.println("Error: Invalid choice");
				}
			} catch (NumberFormatException ex) {
				System.out.println("Error: Invalid input");
			} catch (Exception ex) {
				System.out.println("Error: " + ex.getMessage());
			}
		}
	}

	private void printTodo(Todo todo) {
		System.out.println("Todo " + todo.getId() + ": " + todo.getTitle() + ", " +
				(todo.getDueDate() != null ? todo.getDueDate() + ", " : "") +
				(todo.isCompleted() ? "completed" : "pending"));
	}
}
