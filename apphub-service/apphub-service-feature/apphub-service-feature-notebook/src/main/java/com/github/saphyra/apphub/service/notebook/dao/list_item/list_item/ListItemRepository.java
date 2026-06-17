package com.github.saphyra.apphub.service.notebook.dao.list_item.list_item;

import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.notebook.config.NotebookDynamoDbConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.DELETE_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.FIND_LIST_ITEM_BY_ID;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.GET_LIST_ITEMS_BY_USER_ID;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.GET_LIST_ITEMS_BY_USER_ID_AND_PARENT;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.GET_LIST_ITEMS_BY_USER_ID_AND_TYPE;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.SAVE_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PARENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_TYPE;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.GSI_PK_PARENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.GSI_PK_TYPE;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_USER;

@Component
@Slf4j
class ListItemRepository extends DynamoDbRepository {
    private final ListItemMapper mapper;

    ListItemRepository(NotebookDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, ListItemMapper mapper) {
        super(configuration.getListItemTableName(), context);
        this.mapper = mapper;
    }

    void save(ListItemEntity listItem) {
        Map<String, AttributeValue> item = mapper.convertDomain(listItem);
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build();

        putItem(request, SAVE_LIST_ITEM);
    }

    List<ListItemEntity> getByUserIdAndParent(String userId, String parent) {
        String parentValue = Optional.ofNullable(parent)
            .orElse("");

        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .indexName(GSI_PK_PARENT)
            .keyConditionExpression("#pk = :userId AND #parent = :parent")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#parent", COLUMN_PARENT
            ))
            .expressionAttributeValues(Map.of(
                ":userId", AttributeValue.builder().s(PREFIX_USER + userId).build(),
                ":parent", AttributeValue.builder().s(PREFIX_LIST_ITEM + parentValue).build()
            ))
            .build();

        return query(request, GET_LIST_ITEMS_BY_USER_ID_AND_PARENT)
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    List<ListItemEntity> getByUserIdAndType(String userId, String type) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .indexName(GSI_PK_TYPE)
            .keyConditionExpression("#pk = :userId AND #type = :type")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#type", COLUMN_TYPE
            ))
            .expressionAttributeValues(Map.of(
                ":userId", AttributeValue.builder().s(PREFIX_USER + userId).build(),
                ":type", AttributeValue.builder().s(type).build()
            ))
            .build();

        return query(request, GET_LIST_ITEMS_BY_USER_ID_AND_TYPE)
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    Optional<ListItemEntity> findById(String userId, String listItemId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build()
            ))
            .build();

        return getItem(request, FIND_LIST_ITEM_BY_ID)
            .map(mapper::convertEntity);
    }

    List<ListItemEntity> getByUserId(String userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :userId AND begins_with(#sk, :listItem)")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":userId", AttributeValue.builder().s(PREFIX_USER + userId).build(),
                ":listItem", AttributeValue.builder().s(PREFIX_LIST_ITEM).build()
            ))
            .build();

        return query(request, GET_LIST_ITEMS_BY_USER_ID)
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    void delete(String userId, String listItemId) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build()
            ))
            .build();

        deleteItem(request, DELETE_LIST_ITEM);
    }
}
