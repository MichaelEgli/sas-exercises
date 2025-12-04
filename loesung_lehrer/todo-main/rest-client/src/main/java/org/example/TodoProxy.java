package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class TodoProxy {

	private final RestClient restClient;

	public TodoProxy(@Value("${service.baseUrl}") String baseUrl) {
		restClient = RestClient.create(baseUrl);
	}

	public List<Todo> getTodos() {
		return restClient.get().accept(APPLICATION_JSON)
				.retrieve().body(new ParameterizedTypeReference<>() {});
	}

	public Todo findTodo(long id) throws TodoNotFoundException {
		try {
			return restClient.get().uri("/{id}", id).accept(APPLICATION_JSON).retrieve().body(Todo.class);
		} catch (HttpClientErrorException.NotFound ex) {
			throw new TodoNotFoundException(ex.getResponseBodyAs(ProblemDetail.class).getDetail());
		}
	}

	public Todo addTodo(String title, LocalDate dueDate) {
		TodoDto todoDto = new TodoDto(title, dueDate);
		return restClient.post().contentType(APPLICATION_JSON).body(todoDto).retrieve().body(Todo.class);
	}

	public Todo updateTodo(Todo todo) throws TodoNotFoundException {
		try {
			return restClient.put().uri("/{id}", todo.getId())
					.contentType(APPLICATION_JSON).accept(APPLICATION_JSON).body(todo)
					.retrieve().body(Todo.class);
		} catch (HttpClientErrorException.NotFound ex) {
			throw new TodoNotFoundException(ex.getResponseBodyAs(ProblemDetail.class).getDetail());
		}
	}

	public void removeTodo(long id) {
		restClient.delete().uri("/{id}", id).retrieve().toBodilessEntity();
	}
}
