package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.service.notebook.config.NotebookDynamoDbConfiguration;
import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PARENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_TYPE;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.GSI_USER_ID_PARENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.GSI_USER_ID_TYPE;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_USER;

@Component
@Profile("!test")
@Slf4j
class CommonListItemRepository {
    private final DynamoDbClient client;
    private final String tableName;

    CommonListItemRepository(DynamoDbClient dynamoDbClient, NotebookDynamoDbConfiguration configuration) {
        this.client = dynamoDbClient;
        this.tableName = configuration.getTableName();
    }

    void deleteByListItemId(String userId, String listItemId) {
        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#userId = :userId AND begins_with(#sk, :listItemId)")
            .expressionAttributeNames(Map.of(
                "#userId", COLUMN_USER_ID,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":userId", AttributeValue.builder().s(PREFIX_USER + userId).build(),
                ":listItemId", AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build()
            ))
            .build();

        List<BiWrapper<String, String>> items = client.query(queryRequest)
            .items()
            .stream()
            .map(map -> new BiWrapper<>(
                map.get(COLUMN_USER_ID).s(),
                map.get(COLUMN_SK).s()
            ))
            .toList();

        Lists.partition(items, Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE)
            .forEach(batch -> {
                List<WriteRequest> requests = batch.stream()
                    .map(item -> Map.of(
                        COLUMN_USER_ID, AttributeValue.builder().s(item.getEntity1()).build(),
                        COLUMN_SK, AttributeValue.builder().s(item.getEntity2()).build()
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
            });
    }

    @PostConstruct
    void createListItemTable() {
        try {
            client.describeTable(builder -> builder.tableName(tableName));
            log.info("DynamoDb table '{}' already exists", tableName);
        } catch (ResourceNotFoundException e) {
            log.info("Creating DynamoDb table '{}'", tableName);

            CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .tableName(tableName)
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName(COLUMN_USER_ID)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName(COLUMN_SK)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName(COLUMN_PARENT)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName(COLUMN_TYPE)
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName(COLUMN_USER_ID)
                        .keyType(KeyType.HASH)
                        .build(),
                    KeySchemaElement.builder()
                        .attributeName(COLUMN_SK)
                        .keyType(KeyType.RANGE)
                        .build()
                )
                .globalSecondaryIndexes(
                    GlobalSecondaryIndex.builder()
                        .indexName(GSI_USER_ID_PARENT)
                        .keySchema(
                            KeySchemaElement.builder()
                                .attributeName(COLUMN_USER_ID)
                                .keyType(KeyType.HASH)
                                .build(),
                            KeySchemaElement.builder()
                                .attributeName(COLUMN_PARENT)
                                .keyType(KeyType.RANGE)
                                .build()
                        )
                        .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                        .build(),
                    GlobalSecondaryIndex.builder()
                        .indexName(GSI_USER_ID_TYPE)
                        .keySchema(
                            KeySchemaElement.builder()
                                .attributeName(COLUMN_USER_ID)
                                .keyType(KeyType.HASH)
                                .build(),
                            KeySchemaElement.builder()
                                .attributeName(COLUMN_TYPE)
                                .keyType(KeyType.RANGE)
                                .build()
                        )
                        .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

            client.createTable(createTableRequest);

            client.waiter()
                .waitUntilTableExists(builder -> builder.tableName(tableName));
        }
    }
}
