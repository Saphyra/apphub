package com.github.saphyra.apphub.service.notebook.dao.list_item.list_item;

import com.github.saphyra.apphub.service.notebook.config.NotebookDynamoDbConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.lib.common_domain.Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PARENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_TYPE;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.GSI_USER_ID_PARENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.GSI_USER_ID_TYPE;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_USER;

@Component
@Slf4j
class ListItemRepository {
    private final DynamoDbClient client;
    private final ListItemMapper mapper;
    private final String tableName;

    ListItemRepository(DynamoDbClient dynamoDbClient, ListItemMapper listItemMapper, NotebookDynamoDbConfiguration configuration) {
        this.client = dynamoDbClient;
        this.mapper = listItemMapper;
        this.tableName = configuration.getTableName();
    }

    void save(ListItemEntity listItem) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapper.convertDomain(listItem))
            .build();

        client.putItem(request);
    }

    List<ListItemEntity> getByUserIdAndParent(String userId, String parent) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .indexName(GSI_USER_ID_PARENT)
            .keyConditionExpression("#pk = :userId AND #parent = :parent")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_USER_ID,
                "#parent", COLUMN_PARENT
            ))
            .expressionAttributeValues(Map.of(
                ":userId", AttributeValue.builder().s(PREFIX_USER + userId).build(),
                ":parent", AttributeValue.builder().s(parent).build()
            ))
            .build();

        return client.query(request)
            .items()
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    List<ListItemEntity> getByUserIdAndType(String userId, String type) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .indexName(GSI_USER_ID_TYPE)
            .keyConditionExpression("#userId = :userId AND #type = :type")
            .expressionAttributeNames(Map.of(
                "#userId", COLUMN_USER_ID,
                "#type", COLUMN_TYPE
            ))
            .expressionAttributeValues(Map.of(
                ":userId", AttributeValue.builder().s(PREFIX_USER + userId).build(),
                ":type", AttributeValue.builder().s(type).build()
            ))
            .build();

        return client.query(request)
            .items()
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    Optional<ListItemEntity> findById(String userId, String listItemId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build()
            ))
            .build();

        return Optional.of(client.getItem(request))
            .filter(GetItemResponse::hasItem)
            .map(GetItemResponse::item)
            .map(mapper::convertEntity);
    }

    List<ListItemEntity> getByUserId(String userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#userId = :userId")
            .expressionAttributeNames(Map.of("#userId", COLUMN_USER_ID))
            .expressionAttributeValues(Map.of(":userId", AttributeValue.builder().s(PREFIX_USER + userId).build()))
            .build();

        return client.query(request)
            .items()
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    List<ListItemEntity> getByIds(String userId, List<String> listItemIds) {
        if (listItemIds.size() > DYNAMO_DB_QUERY_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Too many list item ids. Max batch size is " + DYNAMO_DB_QUERY_MAX_BATCH_SIZE);
        }

        List<Map<String, AttributeValue>> keys = listItemIds.stream()
            .map(listItemId -> Map.of(
                COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build()
            ))
            .toList();


        BatchGetItemRequest request = BatchGetItemRequest.builder()
            .requestItems(Map.of(
                tableName,
                KeysAndAttributes.builder()
                    .keys(keys)
                    .build()
            ))
            .build();

        return client.batchGetItem(request)
            .responses()
            .values()
            .stream()
            .flatMap(List::stream)
            .map(mapper::convertEntity)
            .toList();
    }

    void delete(String userId, String listItemId) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build()
            ))
            .build();

        client.deleteItem(request);
    }
}
