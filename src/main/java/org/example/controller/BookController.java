package org.example.controller;

import org.example.entity.Book;
import org.example.pojo.AggregateRequestBody;
import org.example.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.createBook(book);
    }

    @GetMapping(path = "/aggregate", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getAllBooks() {
        return bookService.getForAggregation();
    }

    @PostMapping(path = "/aggregate/generic", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<String> applyAggregationGeneric(@RequestBody AggregateRequestBody aggregateRequestBody) {
        return bookService.genericAggregation(aggregateRequestBody);
    }

    @GetMapping
    public List<Book> getAllForAggregation() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable String id) {
        return bookService.getBookById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable String id, @RequestBody Book book) {
        book.setId(id);
        return bookService.updateBook(book);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
    }
}