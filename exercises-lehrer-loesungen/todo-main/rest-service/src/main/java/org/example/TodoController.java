package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/todos")
public class TodoController {

	private static final Logger log = LoggerFactory.getLogger(TodoController.class);

	private final TodoRepository todoRepository;

	public TodoController(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	public List<Todo> getTodos() {
		return todoRepository.findAll();
	}

	@GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
	public Todo findTodo(@PathVariable long id) throws TodoNotFoundException {
		return todoRepository.findById(id)
				.orElseThrow(() -> new TodoNotFoundException("Todo with id " + id + " not found"));
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Todo> addTodo(@RequestBody TodoDto todoDto) {
		Todo todo = new Todo(todoDto.title(), todoDto.dueDate());
		todo = todoRepository.save(todo);
		log.info("Todo with id {} added", todo.getId());
		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(todo.getId()).toUri();
		return ResponseEntity.created(location).body(todo);
	}

	@PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public Todo updateTodo(@PathVariable long id, @RequestBody Todo todo) throws TodoNotFoundException {
		if (!todoRepository.existsById(id))
			throw new TodoNotFoundException("Todo with id " + id + " not found");
		if (!Objects.equals(id, todo.getId()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todo has a non-matching id");
		todo = todoRepository.save(todo);
		log.info("Todo with id {} updated", todo.getId());
		return todo;
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeTodo(@PathVariable long id) {
		todoRepository.findById(id).ifPresent(todoRepository::delete);
		log.info("Todo with id {} removed", id);
	}
}
