package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TodoService {

	private static final Logger log = LoggerFactory.getLogger(TodoService.class);

	private final TodoRepository todoRepository;

	public TodoService(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

	public List<Todo> getTodos() {
		return todoRepository.findAll();
	}

	public Todo findTodo(long id) throws TodoNotFoundException {
		return todoRepository.findById(id)
				.orElseThrow(() -> new TodoNotFoundException("Todo with id " + id + " not found"));
	}

	public Todo addTodo(String title, LocalDate dueDate) {
		Todo todo = new Todo(title, dueDate);
		todo = todoRepository.save(todo);
		log.info("Todo with id {} added", todo.getId());
		return todo;
	}

	public Todo updateTodo(Todo todo) throws TodoNotFoundException {
		if (!todoRepository.existsById(todo.getId()))
			throw new TodoNotFoundException("Todo with id " + todo.getId() + " not found");
		todo = todoRepository.save(todo);
		log.info("Todo with id {} updated", todo.getId());
		return todo;
	}

	public void removeTodo(long id) {
		todoRepository.findById(id).ifPresent(todoRepository::delete);
		log.info("Todo with id {} removed", id);
	}
}
