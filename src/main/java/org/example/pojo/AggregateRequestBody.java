package org.example.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AggregateRequestBody {
    private String opName;
    private String expression;
    private String expectedFieldName;
    private String collectionName;
}
