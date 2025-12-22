package com.bfh.domi.catalog.catalog.repository;

import com.bfh.domi.catalog.catalog.model.Book;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Set;

public interface BookRepositoryCustom {

    @EntityGraph
    List<Book> findBooksByKeywords(Set<String> keywords);
}
