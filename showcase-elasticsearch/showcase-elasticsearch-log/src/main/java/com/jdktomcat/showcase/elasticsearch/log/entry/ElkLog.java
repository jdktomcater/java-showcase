package com.jdktomcat.showcase.elasticsearch.log.entry;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "fat-txzf-2026.01.03")
public class ElkLog {
    @Id
    private String id;

    private String index;

    private String type;

    private String app;

    private String app_name;

    private String hostname;

    private String message;

    private String tags;

}
