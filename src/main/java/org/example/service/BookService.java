package org.example.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.BookRepository;
import org.example.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookService {

    private final MongoTemplate mongoTemplate;
    private final BookRepository bookRepository;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    public BookService(MongoTemplate mongoTemplate, BookRepository bookRepository) {
        this.mongoTemplate = mongoTemplate;
        this.bookRepository = bookRepository;
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getForAggregation()
    {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project()
                        .andInclude("author", "price")
                        .and("title").toUpper().as("title")
        );
        Timer.Sample sample = Timer.start(meterRegistry);
        AggregationResults<Book> results = mongoTemplate.aggregate(aggregation, "books", Book.class);
        sample.stop(Timer.builder("mongodb.operation.time")
                .tag("operation", "aggregate")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry));
        List<Book> ans =  results.getMappedResults();
        log.info("list size is {}",ans.size());
        return ans;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id);
    }

    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }
}