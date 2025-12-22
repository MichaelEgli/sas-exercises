package com.bfh.domi.catalog.catalog.converter;

import com.bfh.domi.catalog.catalog.dto.BookDto;
import com.bfh.domi.catalog.catalog.model.Book;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BookDtoConverter implements Converter<BookDto, Book> {
    @Override
    public @Nullable Book convert(BookDto source) {
        Book book = new Book();
        book.setIsbn(source.isbn());
        book.setTitle(source.title());
        book.setAuthors(source.authors());
        book.setPublisher(source.publisher());
        book.setPublicationYear(source.publicationYear());
        book.setNumberOfPages(source.numberOfPages());
        book.setDescription(source.description());
        book.setImageUrl(source.imageUrl());
        book.setPrice(source.price());
        return book;
    }
}
