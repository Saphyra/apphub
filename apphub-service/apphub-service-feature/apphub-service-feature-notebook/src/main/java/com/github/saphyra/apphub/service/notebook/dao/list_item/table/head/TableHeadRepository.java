package com.github.saphyra.apphub.service.notebook.dao.list_item.table.head;

import com.github.saphyra.apphub.service.notebook.config.NotebookDynamoDbConfiguration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_TABLE_HEAD;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_USER;

@Component
class TableHeadRepository {
    private final DynamoDbClient client;
    private final TableHeadMapper mapper;
    private final String tableName;

    TableHeadRepository(DynamoDbClient dynamoDbClient, TableHeadMapper tableHeadMapper, NotebookDynamoDbConfiguration configuration) {
        this.client = dynamoDbClient;
        this.mapper = tableHeadMapper;
        this.tableName = configuration.getTableName();
    }

    void save(TableHeadEntity tableHead) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapper.convertDomain(tableHead))
            .build();

        client.putItem(request);
    }

    void delete(String userId, String listItemId) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .key(Map.of(
                COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId + "|" + PREFIX_TABLE_HEAD).build()
            ))
            .build();

        client.deleteItem(request);
    }

    public Optional<TableHeadEntity> findByListItemId(String userId, String listItemId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId + "|" + PREFIX_TABLE_HEAD).build()
            ))
            .build();

        return Optional.of(client.getItem(request))
            .filter(GetItemResponse::hasItem)
            .map(GetItemResponse::item)
            .map(mapper::convertEntity);
    }
}
