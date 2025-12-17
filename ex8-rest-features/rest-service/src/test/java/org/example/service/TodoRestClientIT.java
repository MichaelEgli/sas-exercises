package org.example.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TodoRestClientIT {

	private static final String BASE_PATH = "/todos";

	private final RestClient restClient;

	public TodoRestClientIT(@LocalServerPort int port) {
		restClient = RestClient.create("http://localhost:" + port);
	}

	@Test
	public void addTodo() {
		Todo todo = new Todo("Test", LocalDate.now());
		ResponseEntity<Todo> response = restClient.post().uri(BASE_PATH).body(todo).retrieve().toEntity(Todo.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody().getId()).isNotNull();
	}

	@Test
	public void getTodos() {
		ResponseEntity<Todo[]> response = restClient.get().uri(BASE_PATH).retrieve().toEntity(Todo[].class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getHeaders().getContentType()).isEqualTo(APPLICATION_JSON);
		Assertions.assertThat(response.getBody()).isNotEmpty();
	}

	@Test
	public void findTodo() {
		ResponseEntity<Todo> response = restClient.get().uri("/todos/{id}", 1).retrieve().toEntity(Todo.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getHeaders().getContentType()).isEqualTo(APPLICATION_JSON);
		assertThat(response.getBody().getId()).isEqualTo(1);
	}

	@Test
	public void completeTodo() {
		Todo todo = new Todo("Test", LocalDate.now());
		todo.setId(2L);
		todo.setCompleted(true);
		ResponseEntity<Void> response = restClient.put().uri("/todos/{id}", 2).body(todo).retrieve().toBodilessEntity();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void deleteTodo() {
		ResponseEntity<Void> response = restClient.delete().uri("/todos/{id}", 3).retrieve().toBodilessEntity();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
}
