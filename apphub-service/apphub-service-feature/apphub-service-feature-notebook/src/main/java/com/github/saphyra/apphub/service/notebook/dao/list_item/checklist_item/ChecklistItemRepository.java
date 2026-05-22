package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.service.notebook.config.NotebookDynamoDbConfiguration;
import lombok.extern.slf4j.Slf4j;
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

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_CHECKLIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_USER;

@Component
@Slf4j
class ChecklistItemRepository {
    private final DynamoDbClient client;
    private final ChecklistItemMapper mapper;
    private final String tableName;

    ChecklistItemRepository(DynamoDbClient dynamoDbClient, ChecklistItemMapper mapper, NotebookDynamoDbConfiguration configuration) {
        this.client = dynamoDbClient;
        this.mapper = mapper;
        this.tableName = configuration.getTableName();
    }

    Optional<ChecklistItemEntity> findById(String userId, String listItemId, String checklistItemId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_USER_ID, AttributeValue.builder().s(assemblePartitionKey(userId)).build(),
                COLUMN_SK, AttributeValue.builder().s(assembleSearchKey(listItemId, checklistItemId)).build()
            ))
            .build();

        return Optional.of(client.getItem(request))
            .filter(GetItemResponse::hasItem)
            .map(GetItemResponse::item)
            .map(mapper::convertEntity);
    }

    void delete(String userId, String listItemId, String checklistItemId) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_USER_ID, AttributeValue.builder().s(assemblePartitionKey(userId)).build(),
                COLUMN_SK, AttributeValue.builder().s(assembleSearchKey(listItemId, checklistItemId)).build()
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

    List<ChecklistItemEntity> getByListItemId(String userId, String listItemId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#userId = :userId AND begins_with(#sk, :listItemId)")
            .expressionAttributeNames(Map.of(
                "#userId", COLUMN_USER_ID,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":userId", AttributeValue.builder().s(assemblePartitionKey(userId)).build(),
                ":listItemId", AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build()
            ))
            .build();

        return client.query(request)
            .items()
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    /**
     * @param ids TriWrapper<UserId, ListItemId, ChecklistItemId>
     */
    void delete(List<TriWrapper<String, String, String>> ids) {
        if (ids.size() > Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch size must be less than %d".formatted(Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE));
        }

        List<WriteRequest> requests = ids.stream()
            .map(item -> Map.of(
                COLUMN_USER_ID, AttributeValue.builder().s(assemblePartitionKey(item.getEntity1())).build(),
                COLUMN_SK, AttributeValue.builder().s(assembleSearchKey(item.getEntity2(), item.getEntity3())).build()
            ))
            .map(key -> DeleteRequest.builder().key(key).build())
            .map(deleteRequest -> WriteRequest.builder().deleteRequest(deleteRequest).build())
            .toList();

        BatchWriteItemRequest batchRequest = BatchWriteItemRequest.builder()
            .requestItems(Map.of(tableName, requests))
            .build();

        BatchWriteItemResponse response = client.batchWriteItem(batchRequest);

        if (response.hasUnprocessedItems()) {
            //TODO handle
        }
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

        BatchWriteItemRequest batchRequest = BatchWriteItemRequest.builder()
            .requestItems(Map.of(tableName, requests))
            .build();

        BatchWriteItemResponse response = client.batchWriteItem(batchRequest);

        if (response.hasUnprocessedItems()) {
            //TODO handle
        }
    }

    private static String assemblePartitionKey(String userId) {
        return PREFIX_USER + userId;
    }

    private String assembleSearchKey(String listItemId, String checklistItemId) {
        return "%s%s|%s%s".formatted(PREFIX_LIST_ITEM, listItemId, PREFIX_CHECKLIST_ITEM, checklistItemId);
    }
}
