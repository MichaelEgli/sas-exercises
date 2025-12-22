package com.bfh.domi.catalog.catalog.converter;

import com.bfh.domi.catalog.catalog.integration.google.dto.IndustryIdentifier;
import com.bfh.domi.catalog.catalog.integration.google.dto.Volume;
import com.bfh.domi.catalog.catalog.model.Book;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class VolumeConverter implements Converter<Volume, Book> {

    private final static Logger LOG = LoggerFactory.getLogger(VolumeConverter.class);

    @Value("${bookstore.googlebooks.isbntype:ISBN_13}")
    private String isbnType;

    @Override
    public @Nullable Book convert(Volume volume) {
        if (volume == null || volume.volumeInfo() == null) {
            return null;
        }

        Book book = new Book();
        var volumeInfo = volume.volumeInfo();
        var saleInfo = volume.saleInfo();

        book.setIsbn(extractIsbn(volumeInfo.industryIdentifiers()));
        book.setTitle(volumeInfo.title());
        book.setSubtitle(volumeInfo.subtitle());
        book.setAuthors(volumeInfo.authors() != null ? String.join(", ", volumeInfo.authors()) : null);
        book.setPublisher(volumeInfo.publisher());
        book.setPublicationYear(extractYear(volumeInfo.publishedDate()));
        book.setNumberOfPages(volumeInfo.pageCount());
        book.setDescription(volumeInfo.description());
        book.setImageUrl(volumeInfo.imageLinks() != null ? volumeInfo.imageLinks().thumbnail() : null);
        book.setPrice(saleInfo.listPrice() != null ? BigDecimal.valueOf(saleInfo.listPrice().amount()) : null);

        return book;
    }

    private String extractIsbn(IndustryIdentifier[] identifiers) {
        if (identifiers == null || identifiers.length == 0) {
            return null;
        }

        return Arrays.stream(identifiers)
                .filter(id -> isbnType.equals(id.type()))
                .map(IndustryIdentifier::identifier)
                .findFirst()
                .orElse(null);
    }

    private Integer extractYear(String publishedDate) {
        if (publishedDate == null || publishedDate.isEmpty()) {
            return null;
        }

        try {
            // Extract year from various formats: "2017", "2017-01", "2017-01-15"
            String yearString = publishedDate.split("-")[0];
            return Integer.parseInt(yearString);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            LOG.warn("Could not parse year from publishedDate: {}", publishedDate, e);
            return null;
        }
    }
}
