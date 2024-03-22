package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.example.pojo.AggregateRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ApiService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    private MeterRegistry meterRegistry;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ApiService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
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

    public List<String> genericAggregationv2(AggregateRequestBody aggregateRequestBody) throws IOException {
        // Parse the JSON string to a list of Documents
        List<Document> aggregationStages = parseJsonPipeline(aggregateRequestBody.getExpression());

        // Convert Documents to AggregationOperations
        List<AggregationOperation> operations = convertToAggregationOperations(aggregationStages);

        // Create and execute the aggregation
        Aggregation aggregation = Aggregation.newAggregation(operations);
        Timer.Sample sample = Timer.start(meterRegistry);
        AggregationResults<String> results = mongoTemplate.aggregate(aggregation, aggregateRequestBody.getCollectionName(), String.class);
        sample.stop(Timer.builder("mongodb.operation.time")
                .tag("operation", "aggregate_"+aggregateRequestBody.getOpName())
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry));
        return results.getMappedResults();
    }

    private List<Document> parseJsonPipeline(String jsonPipeline) throws IOException {
        // Assumes the input JSON string is a valid representation of a MongoDB aggregation pipeline
        return objectMapper.readValue(jsonPipeline, new TypeReference<List<Document>>() {});
    }

    private List<AggregationOperation> convertToAggregationOperations(List<Document> aggregationStages) {
        List<AggregationOperation> operations = new ArrayList<>();
        for (Document stage : aggregationStages) {
            operations.add(context -> stage);
        }
        return operations;
    }
}