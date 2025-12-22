package com.bfh.domi.catalog.catalog.controller;

import com.bfh.domi.catalog.catalog.dto.BookDto;
import com.bfh.domi.catalog.catalog.exception.BookAlreadyExistsException;
import com.bfh.domi.catalog.catalog.exception.BookNotFoundException;
import com.bfh.domi.catalog.catalog.model.Book;
import com.bfh.domi.catalog.catalog.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/books")
@Validated
public class CatalogController {

    private final CatalogService catalogService;
    private final ConversionService conversionService;

    public CatalogController(CatalogService catalogService, ConversionService conversionService) {
        this.catalogService = catalogService;
        this.conversionService = conversionService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a book to the catalog")
    @ApiResponse(responseCode = "201", description = "Book added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid book data provided")
    @ApiResponse(responseCode = "409", description = "Book with the given ISBN already exists")
    public ResponseEntity<BookDto> addBook(@Valid @RequestBody BookDto bookDto) throws BookAlreadyExistsException {
        Book book = conversionService.convert(bookDto, Book.class);
        Book savedBook = catalogService.addBook(book);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedBook.getIsbn()).toUri();
        return ResponseEntity.created(location).body(conversionService.convert(savedBook, BookDto.class));
    }

    @GetMapping(path = "/{isbn}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find a book by its ISBN")
    @ApiResponse(responseCode = "200", description = "Book found successfully")
    @ApiResponse(responseCode = "404", description = "Book with the given ISBN not found")
    public BookDto findBook(@PathVariable String isbn) throws BookNotFoundException {
        Book book = catalogService.findBook(isbn);
        return conversionService.convert(book, BookDto.class);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search books by keywords")
    @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    public List<BookDto> searchBooks(@RequestParam @NotBlank String keywords) {
        List<Book> books = catalogService.searchBook(keywords);
        List<BookDto> bookDtos = new java.util.ArrayList<>();
        for (Book book : books) {
            bookDtos.add(conversionService.convert(book, BookDto.class));
        }
        return bookDtos;
    }

    @PutMapping(path = "/{isbn}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation (summary = "Update a book in the catalog")
    @ApiResponse(responseCode = "200", description = "Book updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid book data provided")
    @ApiResponse(responseCode = "404", description = "Book with the given ISBN not found")
    public BookDto updateBook(@PathVariable String isbn, @Valid @RequestBody BookDto bookDto) throws BookNotFoundException {
        if (!isbn.equals(bookDto.isbn())) {
            throw new IllegalArgumentException("ISBN in path and body do not match");
        }
        Book book = conversionService.convert(bookDto, Book.class);
        Book updatedBook = catalogService.updateBook(book);
        return conversionService.convert(updatedBook, BookDto.class);
    }
}
