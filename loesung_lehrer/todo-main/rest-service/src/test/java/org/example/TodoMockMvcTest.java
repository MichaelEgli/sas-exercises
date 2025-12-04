package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
public class TodoMockMvcTest {

	private static final String BASE_PATH = "/todos";

	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private TodoRepository repository;
	@Autowired
	private JsonMapper jsonMapper;

	@BeforeEach
	public void configureRepository() {
		Todo todo = new Todo("Test", LocalDate.now());
		todo.setId(1L);
		Mockito.when(repository.findAll()).thenReturn(singletonList(todo));
		Mockito.when(repository.findById(any())).thenReturn(Optional.of(todo));
		Mockito.when(repository.existsById(any())).thenReturn(true);
		Mockito.when(repository.save(any())).thenReturn(todo);
	}

	@Test
	public void addTodo() throws Exception {
		Todo todo = new Todo("Test", LocalDate.now());
		mockMvc.perform(post(BASE_PATH).contentType(APPLICATION_JSON).content(asJson(todo)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(APPLICATION_JSON))
				.andExpect(jsonPath("id").isNumber());
	}

	@Test
	public void getTodos() throws Exception {
		mockMvc.perform(get(BASE_PATH))
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON))
				.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void findTodo() throws Exception {
		mockMvc.perform(get(BASE_PATH + "/" + 1))
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON))
				.andExpect(jsonPath("id").value(1));
	}

	@Test
	public void completeTodo() throws Exception {
		Todo todo = new Todo("Test", LocalDate.now());
		todo.setId(2L);
		todo.setCompleted(true);
		mockMvc.perform(put(BASE_PATH + "/" + 2).contentType(APPLICATION_JSON).content(asJson(todo)))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteTodo() throws Exception {
		mockMvc.perform(delete(BASE_PATH + "/" + 3))
				.andExpect(status().isNoContent());
	}

	private String asJson(Object object) {
		return jsonMapper.writeValueAsString(object);
	}
}
