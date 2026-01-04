package com.jdktomcat.showcase.elasticsearch.log.entry;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "fat-txzf*")
public class ElkLog {
    @Id
    private String id;
    private String log_time;
    private String app_name;
    private String message;
}
