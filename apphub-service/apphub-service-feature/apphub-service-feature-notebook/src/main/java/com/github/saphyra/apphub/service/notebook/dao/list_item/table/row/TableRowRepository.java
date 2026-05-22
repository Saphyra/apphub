package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.service.notebook.config.NotebookDynamoDbConfiguration;
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
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_TABLE_ROW;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_USER;

@Component
class TableRowRepository {
    private final DynamoDbClient client;
    private final TableRowMapper mapper;
    private final String tableName;

    TableRowRepository(DynamoDbClient dynamoDbClient, TableRowMapper mapper, NotebookDynamoDbConfiguration configuration) {
        this.client = dynamoDbClient;
        this.mapper = mapper;
        this.tableName = configuration.getTableName();
    }

    void delete(String userId, String listItemId, String tableRowId) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .key(Map.of(
                COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId + "|" + PREFIX_TABLE_ROW + tableRowId).build()
            ))
            .build();

        client.deleteItem(request);
    }

    void save(TableRowEntity row) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapper.convertDomain(row))
            .build();

        client.putItem(request);
    }

    public void delete(String userId, String listItemId, List<String> rowIds) {
        if (rowIds.size() > Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch delete size cannot be greater than " + Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE);
        }

        List<WriteRequest> requests = rowIds.stream()
            .map(rowId -> Map.of(
                COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId + "|" + PREFIX_TABLE_ROW + rowId).build()
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

    public void save(List<TableRowEntity> rows) {
        if (rows.size() > Constants.DYNAMO_DB_INSERT_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch size must be less than %d".formatted(Constants.DYNAMO_DB_INSERT_MAX_BATCH_SIZE));
        }

        List<WriteRequest> requests = rows.stream()
            .map(mapper::convertDomain)
            .map(row -> PutRequest.builder().item(row).build())
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

    public Optional<TableRowEntity> findById(String userId, String listItemId, String rowId) {
        GetItemRequest request = GetItemRequest.builder()
            .key(Map.of(
                COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId + "|" + PREFIX_TABLE_ROW + rowId).build()
            ))
            .build();

        return Optional.of(client.getItem(request))
            .filter(GetItemResponse::hasItem)
            .map(GetItemResponse::item)
            .map(mapper::convertEntity);
    }

    public List<TableRowEntity> getByListItemId(String userId, String listItemId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#userId = :userId AND begins_with(#sk, :listItemId)")
            .expressionAttributeNames(Map.of(
                "#userId", COLUMN_USER_ID,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":userId", AttributeValue.builder().s(PREFIX_USER + userId).build(),
                ":listItemId", AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId + "|" + PREFIX_TABLE_ROW).build()
            ))
            .build();

        return client.query(request)
            .items()
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }
}
