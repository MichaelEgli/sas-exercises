package com.bfh.domi.catalog.repos;

import com.bfh.domi.catalog.entities.Book;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface BookRepositoryCustom {

    @EntityGraph
    List<Book> findBooksByKeywords(List<String> keywords);
}
