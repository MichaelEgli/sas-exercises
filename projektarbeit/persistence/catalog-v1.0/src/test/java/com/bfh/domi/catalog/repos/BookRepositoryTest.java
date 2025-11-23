package com.bfh.domi.catalog.repos;

import com.bfh.domi.catalog.entities.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import com.bfh.domi.catalog.TestcontainersConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(BookRepositoryTest.class);

    @Autowired
    private BookRepository bookRepository;


    @Test
    @Sql("/test-data-books.sql")
    void findAllBooks() {

        var books = bookRepository.findAll();
        assertFalse(books.isEmpty());

        log.info("Found {} books in the database", books.size());

        bookRepository.flush();
    }

}