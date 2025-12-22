package com.bfh.domi.catalog.catalog.repository;

import com.bfh.domi.catalog.catalog.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, String>, BookRepositoryCustom {

    // Query 3: Finde das Buch (Book) mit einer bestimmten ISBN-Nummer
    Optional <Book> findByIsbn(String isbn);
}
