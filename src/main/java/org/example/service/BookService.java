package org.example.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.BookRepository;
import org.example.entity.Book;
import org.example.pojo.AggregateRequestBody;
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

    public List<String> getForAggregation()
    {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project()
                        .andExclude("_id")
                        .andExpression("{ $bsonSize: '$a' }").as("bsonSizeOfField")
        );
        Timer.Sample sample = Timer.start(meterRegistry);
        AggregationResults<String> results = mongoTemplate.aggregate(aggregation, "hw", String.class);
        sample.stop(Timer.builder("mongodb.operation.time")
                .tag("operation", "aggregate")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry));
        List<String> ans =  results.getMappedResults();
        return ans;
    }

    public List<String> genericAggregation(AggregateRequestBody aggregateRequestBody)
    {
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.project()
                            .andExclude("_id")
                            .andExpression(aggregateRequestBody.getExpression()).as(aggregateRequestBody.getExpectedFieldName())
            );
            Timer.Sample sample = Timer.start(meterRegistry);
            AggregationResults<String> results = mongoTemplate.aggregate(aggregation, aggregateRequestBody.getCollectionName(), String.class);
            sample.stop(Timer.builder("mongodb.operation.time")
                    .tag("operation", "aggregate_"+aggregateRequestBody.getOpName())
                    .publishPercentiles(0.5, 0.95, 0.99)
                    .register(meterRegistry));
            return results.getMappedResults();
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