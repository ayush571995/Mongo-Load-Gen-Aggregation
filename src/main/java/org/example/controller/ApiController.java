package org.example.controller;

import org.example.pojo.AggregateRequestBody;
import org.example.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@RestController
public class ApiController {
    private final ApiService apiService;

    @Autowired
    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping(path = "/aggregate", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getAggregate() {
        return apiService.getForAggregation();
    }

    @PostMapping(path = "/aggregate/generic", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<String> applyAggregationGeneric(@RequestBody AggregateRequestBody aggregateRequestBody) {
        return apiService.genericAggregation(aggregateRequestBody);
    }

    @PostMapping(path = "/aggregate/pipeline", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<String> applyAggregationGenericV2(@RequestBody AggregateRequestBody aggregateRequestBody) throws IOException {
        return apiService.genericAggregationv2(aggregateRequestBody);
    }
}