package com.github.saphyra.apphub.service.notebook.dao.list_item.table.head;

import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.notebook.config.NotebookDynamoDbConfiguration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.DELETE_TABLE_HEAD;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.FIND_TABLE_HEAD_BY_LIST_ITEM_ID;
import static com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality.SAVE_TABLE_HEAD;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_TABLE_HEAD;

@Component
class TableHeadRepository extends DynamoDbRepository {
    private final TableHeadMapper mapper;

    TableHeadRepository(NotebookDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, TableHeadMapper mapper) {
        super(configuration.getTableName(), context);
        this.mapper = mapper;
    }

    void save(TableHeadEntity tableHead) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapper.convertDomain(tableHead))
            .build();

        putItem(request, SAVE_TABLE_HEAD);
    }

    void delete(String listItemId) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_TABLE_HEAD).build()
            ))
            .build();

        deleteItem(request, DELETE_TABLE_HEAD);
    }

    public Optional<TableHeadEntity> findByListItemId(String listItemId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_TABLE_HEAD).build()
            ))
            .build();

        return getItem(request, FIND_TABLE_HEAD_BY_LIST_ITEM_ID)
            .map(mapper::convertEntity);
    }
}
