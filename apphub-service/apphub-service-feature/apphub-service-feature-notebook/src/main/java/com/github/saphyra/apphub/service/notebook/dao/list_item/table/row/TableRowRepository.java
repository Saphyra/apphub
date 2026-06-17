package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.notebook.config.NotebookDynamoDbConfiguration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.DELETE_TABLE_ROW;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.DELETE_TABLE_ROWS;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.FIND_TABLE_ROW_BY_ID;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.GET_TABLE_ROWS_BY_LIST_ITEM_ID;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.SAVE_TABLE_ROW;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.SAVE_TABLE_ROWS;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_TABLE_ROW;

@Component
class TableRowRepository extends DynamoDbRepository {
    private final TableRowMapper mapper;

    TableRowRepository(NotebookDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, TableRowMapper mapper) {
        super(configuration.getListItemTableName(), context);
        this.mapper = mapper;
    }

    void delete(String listItemId, String tableRowId) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_TABLE_ROW + tableRowId).build()
            ))
            .build();

        deleteItem(request, DELETE_TABLE_ROW);
    }

    void save(TableRowEntity row) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapper.convertDomain(row))
            .build();

        putItem(request, SAVE_TABLE_ROW);
    }

    public void delete(String listItemId, List<String> rowIds) {
        List<WriteRequest> requests = rowIds.stream()
            .map(rowId -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_TABLE_ROW + rowId).build()
            ))
            .map(key -> DeleteRequest.builder().key(key).build())
            .map(deleteRequest -> WriteRequest.builder().deleteRequest(deleteRequest).build())
            .toList();

        batchWrite(requests, DELETE_TABLE_ROWS);
    }

    public void save(List<TableRowEntity> rows) {
        List<WriteRequest> requests = rows.stream()
            .map(mapper::convertDomain)
            .map(row -> PutRequest.builder().item(row).build())
            .map(putRequest -> WriteRequest.builder().putRequest(putRequest).build())
            .toList();

        batchWrite(requests, SAVE_TABLE_ROWS);
    }

    public Optional<TableRowEntity> findById(String listItemId, String rowId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_TABLE_ROW + rowId).build()
            ))
            .build();

        return getItem(request, FIND_TABLE_ROW_BY_ID)
            .map(mapper::convertEntity);
    }

    public List<TableRowEntity> getByListItemId(String listItemId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :listItemId AND begins_with(#sk, :tableRow)")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":listItemId", AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build(),
                ":tableRow", AttributeValue.builder().s(PREFIX_TABLE_ROW).build()
            ))
            .build();

        return query(request, GET_TABLE_ROWS_BY_LIST_ITEM_ID)
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }
}
