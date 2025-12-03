package org.example;

import java.time.LocalDate;

public record TodoDto(String title, LocalDate dueDate) {
}
