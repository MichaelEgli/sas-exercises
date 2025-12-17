package org.example.service;

import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

public record TodoDto(String title, @FutureOrPresent LocalDate dueDate) {
}
