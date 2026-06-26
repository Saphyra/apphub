package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.notebook.config.NotebookDynamoDbConfiguration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.DELETE_CONTENTS_BY_LIST_ITEM_ID_AND_BATCH_INDEXES;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.GET_CONTENTS_BY_LIST_ITEM_ID;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.SAVE_CONTENTS;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_CONTENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;

@Component
class ContentRepository extends DynamoDbRepository {
    private final ContentMapper mapper;

    ContentRepository(NotebookDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, ContentMapper mapper) {
        super(configuration.getListItemTableName(), context);
        this.mapper = mapper;
    }

    void save(List<ContentEntity> contents) {
        List<WriteRequest> requests = contents.stream()
            .map(mapper::convertDomain)
            .map(item -> PutRequest.builder().item(item).build())
            .map(putRequest -> WriteRequest.builder().putRequest(putRequest).build())
            .toList();

        batchWrite(requests, SAVE_CONTENTS);
    }

    void delete(String listItemId, List<Integer> batchIndexes) {
        List<WriteRequest> requests = batchIndexes.stream()
            .map(batchIndex -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_CONTENT + batchIndex).build()
            ))
            .map(key -> DeleteRequest.builder().key(key).build())
            .map(deleteRequest -> WriteRequest.builder().deleteRequest(deleteRequest).build())
            .toList();

        batchWrite(requests, DELETE_CONTENTS_BY_LIST_ITEM_ID_AND_BATCH_INDEXES);
    }

    List<ContentEntity> getByListItemId(String listItemId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :listItemId AND begins_with(#sk, :content)")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":listItemId", AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build(),
                ":content", AttributeValue.builder().s(PREFIX_CONTENT).build()
            ))
            .build();

        return query(request, GET_CONTENTS_BY_LIST_ITEM_ID)
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }
}
