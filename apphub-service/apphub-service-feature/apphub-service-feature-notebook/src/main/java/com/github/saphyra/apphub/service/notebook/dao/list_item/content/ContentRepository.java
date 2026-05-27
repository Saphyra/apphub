package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.config.NotebookDynamoDbConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_CONTENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;

@Component
class ContentRepository {
    private final DynamoDbClient client;
    private final ContentMapper mapper;
    private final String tableName;
    private final SleepService sleepService;
    private final int maxBatchRetryCount;
    private final long batchRetryDelayMs;

    ContentRepository(DynamoDbClient dynamoDbClient, ContentMapper mapper, NotebookDynamoDbConfiguration configuration, SleepService sleepService) {
        this.client = dynamoDbClient;
        this.mapper = mapper;
        this.tableName = configuration.getTableName();
        this.maxBatchRetryCount = configuration.getMaxBatchRetryCount();
        this.batchRetryDelayMs = configuration.getBatchRetryDelayMs();
        this.sleepService = sleepService;
    }

    public void save(List<ContentEntity> contents) {
        if (contents.size() > Constants.DYNAMO_DB_INSERT_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch size must be less than " + Constants.DYNAMO_DB_INSERT_MAX_BATCH_SIZE);
        }

        List<WriteRequest> requests = contents.stream()
            .map(mapper::convertDomain)
            .map(item -> PutRequest.builder().item(item).build())
            .map(putRequest -> WriteRequest.builder().putRequest(putRequest).build())
            .toList();

        batchWrite(0, requests);
    }

    public void delete(String listItemId, List<Integer> batchIndexes) {
        if (batchIndexes.size() > Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch size must be less than " + Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE);
        }

        List<WriteRequest> requests = batchIndexes.stream()
            .map(batchIndex -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_CONTENT + batchIndex).build()
            ))
            .map(key -> DeleteRequest.builder().key(key).build())
            .map(deleteRequest -> WriteRequest.builder().deleteRequest(deleteRequest).build())
            .toList();

        batchWrite(0, requests);
    }

    private void batchWrite(int tryCount, List<WriteRequest> requests) {
        if (tryCount > maxBatchRetryCount) {
            throw ExceptionFactory.reportedException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCode.GENERAL_ERROR, "Batch retry limit exceeded");
        }

        if (tryCount > 0) {
            sleepService.sleep(tryCount * batchRetryDelayMs);
        }

        BatchWriteItemRequest batchRequest = BatchWriteItemRequest.builder()
            .requestItems(Map.of(tableName, requests))
            .build();

        BatchWriteItemResponse response = client.batchWriteItem(batchRequest);

        if (response.hasUnprocessedItems()) {
            response.unprocessedItems()
                .values()
                .forEach(writeRequests -> batchWrite(tryCount + 1, writeRequests));
        }
    }

    public List<ContentEntity> getByListItemId(String listItemId) {
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

        return client.query(request)
            .items()
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }
}
