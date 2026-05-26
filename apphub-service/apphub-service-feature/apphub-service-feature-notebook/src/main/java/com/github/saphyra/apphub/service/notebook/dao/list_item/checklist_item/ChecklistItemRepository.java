package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.config.NotebookDynamoDbConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_CHECKLIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;

@Component
@Slf4j
class ChecklistItemRepository {
    private final DynamoDbClient client;
    private final ChecklistItemMapper mapper;
    private final String tableName;
    private final SleepService sleepService;
    private final int maxBatchRetryCount;
    private final long batchRetryDelayMs;

    ChecklistItemRepository(DynamoDbClient dynamoDbClient, ChecklistItemMapper mapper, NotebookDynamoDbConfiguration configuration, SleepService sleepService) {
        this.client = dynamoDbClient;
        this.mapper = mapper;
        this.tableName = configuration.getTableName();
        this.sleepService = sleepService;
        this.maxBatchRetryCount = configuration.getMaxBatchRetryCount();
        this.batchRetryDelayMs = configuration.getBatchRetryDelayMs();
    }

    Optional<ChecklistItemEntity> findById(String listItemId, String checklistItemId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_CHECKLIST_ITEM + checklistItemId).build()
            ))
            .build();

        return Optional.of(client.getItem(request))
            .filter(GetItemResponse::hasItem)
            .map(GetItemResponse::item)
            .map(mapper::convertEntity);
    }

    void delete(String listItemId, String checklistItemId) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_CHECKLIST_ITEM + checklistItemId).build()
            ))
            .build();

        client.deleteItem(request);
    }

    void save(ChecklistItemEntity checklistItem) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapper.convertDomain(checklistItem))
            .build();

        client.putItem(request);
    }

    List<ChecklistItemEntity> getByListItemId(String listItemId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :listItemId AND begins_with(#sk, :checklistItem)")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":listItemId", AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build(),
                ":checklistItem", AttributeValue.builder().s(PREFIX_CHECKLIST_ITEM).build()
            ))
            .build();

        return client.query(request)
            .items()
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    /**
     * @param ids BiWrapper<ListItemId, ChecklistItemId>
     */
    void delete(List<BiWrapper<String, String>> ids) {
        if (ids.size() > Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch size must be less than %d".formatted(Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE));
        }

        List<WriteRequest> requests = ids.stream()
            .map(item -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + item.getEntity1()).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_CHECKLIST_ITEM + item.getEntity2()).build()
            ))
            .map(key -> DeleteRequest.builder().key(key).build())
            .map(deleteRequest -> WriteRequest.builder().deleteRequest(deleteRequest).build())
            .toList();

        batchWrite(0, requests);
    }

    void save(List<ChecklistItemEntity> items) {
        if (items.size() > Constants.DYNAMO_DB_INSERT_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch size must be less than %d".formatted(Constants.DYNAMO_DB_INSERT_MAX_BATCH_SIZE));
        }

        List<WriteRequest> requests = items.stream()
            .map(mapper::convertDomain)
            .map(item -> PutRequest.builder().item(item).build())
            .map(putRequest -> WriteRequest.builder().putRequest(putRequest).build())
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
}
