package org.example.client;

import java.time.LocalDate;

public record TodoDto(String title, LocalDate dueDate) {
}
