package com.bfh.domi.catalog.repos;

import com.bfh.domi.catalog.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, String>, BookRepositoryCustom {

    // Query 3: Finde das Buch (Book) mit einer bestimmten ISBN-Nummer
    Optional <Book> findByIsbn(String isbn);

}
