package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/todos")
public class TodoController {

	private static final Logger log = LoggerFactory.getLogger(TodoController.class);

	private final TodoRepository todoRepository;

	public TodoController(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

    @GetMapping
	public List<Todo> getTodos() {
		return todoRepository.findAll();
	}

    @GetMapping("/{id}")
	public Todo findTodo(@PathVariable long id) throws TodoNotFoundException {
		return todoRepository.findById(id)
				.orElseThrow(() -> new TodoNotFoundException("Todo with id " + id + " not found"));
	}

    @PostMapping
	public Todo addTodo(@RequestBody TodoDto todoDto) {
  		Todo todo = new Todo(todoDto.title(), todoDto.dueDate());
		todo = todoRepository.save(todo);
		log.info("Todo with id {} added", todo.getId());
		return todo;
	}

    @PutMapping(path = "{id}", consumes = "application/json", produces = "application/json")	public Todo updateTodo(@PathVariable long id, @RequestBody Todo todo) throws TodoNotFoundException {
        if (!todoRepository.existsById(id))
            throw new TodoNotFoundException("Todo with id " + id + " not found");
        if (!Objects.equals(id, todo.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todo has a non-matching id");
        todo = todoRepository.save(todo);
        log.info("Todo with id {} updated", todo.getId());
        return todo;
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeTodo(@PathVariable long id) {
		todoRepository.findById(id).ifPresent(todoRepository::delete);
		log.info("Todo with id {} removed", id);
	}
}
