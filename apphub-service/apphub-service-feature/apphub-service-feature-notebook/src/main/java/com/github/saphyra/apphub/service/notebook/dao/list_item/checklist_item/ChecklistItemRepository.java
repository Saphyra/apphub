package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.notebook.config.NotebookDynamoDbConfiguration;
import lombok.extern.slf4j.Slf4j;
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

import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.DELETE_CHECKLIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.DELETE_CHECKLIST_ITEMS;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.FIND_CHECKLIST_ITEM_BY_ID;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.GET_CHECKLIST_ITEMS_BY_LIST_ITEM_ID;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.SAVE_CHECKLIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.SAVE_CHECKLIST_ITEMS;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_CHECKLIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;

@Component
@Slf4j
class ChecklistItemRepository extends DynamoDbRepository {
    private final ChecklistItemMapper mapper;

    ChecklistItemRepository(NotebookDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, ChecklistItemMapper mapper) {
        super(configuration.getTableName(), context);
        this.mapper = mapper;
    }

    Optional<ChecklistItemEntity> findById(String listItemId, String checklistItemId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_CHECKLIST_ITEM + checklistItemId).build()
            ))
            .build();

        return getItem(request, FIND_CHECKLIST_ITEM_BY_ID)
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

        deleteItem(request, DELETE_CHECKLIST_ITEM);
    }

    void save(ChecklistItemEntity checklistItem) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapper.convertDomain(checklistItem))
            .build();

        putItem(request, SAVE_CHECKLIST_ITEM);
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

        return query(request, GET_CHECKLIST_ITEMS_BY_LIST_ITEM_ID)
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    /**
     * @param ids BiWrapper<ListItemId, ChecklistItemId>
     */
    void delete(List<BiWrapper<String, String>> ids) {
        List<WriteRequest> requests = ids.stream()
            .map(item -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + item.getEntity1()).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_CHECKLIST_ITEM + item.getEntity2()).build()
            ))
            .map(key -> DeleteRequest.builder().key(key).build())
            .map(deleteRequest -> WriteRequest.builder().deleteRequest(deleteRequest).build())
            .toList();

        batchWrite(requests, DELETE_CHECKLIST_ITEMS);
    }

    void save(List<ChecklistItemEntity> items) {
        List<WriteRequest> requests = items.stream()
            .map(mapper::convertDomain)
            .map(item -> PutRequest.builder().item(item).build())
            .map(putRequest -> WriteRequest.builder().putRequest(putRequest).build())
            .toList();

        batchWrite(requests, SAVE_CHECKLIST_ITEMS);
    }
}
