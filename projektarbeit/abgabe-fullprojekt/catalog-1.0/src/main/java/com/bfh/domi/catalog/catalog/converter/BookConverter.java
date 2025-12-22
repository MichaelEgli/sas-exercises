package com.bfh.domi.catalog.catalog.converter;

import com.bfh.domi.catalog.catalog.dto.BookDto;
import com.bfh.domi.catalog.catalog.model.Book;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BookConverter implements Converter<Book, BookDto> {
    @Override
    public @Nullable BookDto convert(Book book) {
        return new BookDto(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthors(),
                book.getPublisher(),
                book.getPublicationYear(),
                book.getNumberOfPages(),
                book.getDescription(),
                book.getImageUrl(),
                book.getPrice()
        );
    }
}
