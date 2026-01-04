package com.jdktomcat.showcase.elasticsearch.base.dao;

import com.alibaba.fastjson2.JSON;
import com.jdktomcat.showcase.elasticsearch.base.entry.UserDocument;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Repository
@Slf4j
public class UserDocumentEsDAO {

    @Autowired
    private RestHighLevelClient client;

    /**
     * createUserIndex
     *
     * @param index index name
     * @return true:success false:fail
     * @throws IOException exception
     */
    public boolean createUserIndex(String index) throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
        );
        createIndexRequest.mapping("{\n" +
                "  \"properties\": {\n" +
                "    \"city\": {\n" +
                "      \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"sex\": {\n" +
                "      \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"name\": {\n" +
                "      \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"id\": {\n" +
                "      \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"age\": {\n" +
                "      \"type\": \"integer\"\n" +
                "    }\n" +
                "  }\n" +
                "}", XContentType.JSON);
        CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        return createIndexResponse.isAcknowledged();
    }

    /**
     * deleteUserIndex
     *
     * @param index index name
     * @return true:success false:fail
     * @throws IOException exception
     */
    public Boolean deleteUserIndex(String index) throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        AcknowledgedResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        return deleteIndexResponse.isAcknowledged();
    }

    public Boolean createUserDocument(UserDocument document,String index) throws Exception {
        UUID uuid = UUID.randomUUID();
        document.setId(uuid.toString());
        IndexRequest indexRequest = new IndexRequest(index)
                .id(document.getId())
                .source(JSON.toJSONString(document), XContentType.JSON);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse.status().equals(RestStatus.OK);
    }

    public Boolean bulkCreateUserDocument(List<UserDocument> documents, String index) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (UserDocument document : documents) {
            String id = UUID.randomUUID().toString();
            document.setId(id);
            IndexRequest indexRequest = new IndexRequest(index)
                    .id(id)
                    .source(JSON.toJSONString(document), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return bulkResponse.status().equals(RestStatus.OK);
    }

    public UserDocument getUserDocument(String id,String index) throws IOException {
        GetRequest getRequest = new GetRequest(index, id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        UserDocument result = new UserDocument();
        if (getResponse.isExists()) {
            String sourceAsString = getResponse.getSourceAsString();
            result = JSON.parseObject(sourceAsString, UserDocument.class);
        } else {
            log.error("没有找到该 id 的文档");
        }
        return result;
    }
}
