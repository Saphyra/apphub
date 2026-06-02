package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.config.NotebookDynamoDbConfiguration;
import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
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
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_TYPE;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.GSI_PK_PARENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.GSI_PK_TYPE;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;

@Component
@Profile("!test")
@Slf4j
class CommonListItemRepository extends DynamoDbRepository {
    private final DynamoDbClient client;
    private final String tableName;
    private final SleepService sleepService;
    private final int maxBatchRetryCount;
    private final long batchRetryDelayMs;

    CommonListItemRepository(DynamoDbClient dynamoDbClient, NotebookDynamoDbConfiguration configuration, SleepService sleepService) {
        this.client = dynamoDbClient;
        this.tableName = configuration.getTableName();
        this.maxBatchRetryCount = configuration.getMaxBatchRetryCount();
        this.batchRetryDelayMs = configuration.getBatchRetryDelayMs();
        this.sleepService = sleepService;
    }

    void deleteByListItemId(String listItemId) {
        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :listItemId")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":listItemId", AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId).build()))
            .build();

        //TODO handle lastEvaluatedKey
        List<BiWrapper<String, String>> items = client.query(queryRequest)
            .items()
            .stream()
            .map(map -> new BiWrapper<>(
                map.get(COLUMN_PK).s(),
                map.get(COLUMN_SK).s()
            ))
            .toList();

        Lists.partition(items, Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE)
            .forEach(batch -> {
                List<WriteRequest> requests = batch.stream()
                    .map(item -> Map.of(
                        COLUMN_PK, AttributeValue.builder().s(item.getEntity1()).build(),
                        COLUMN_SK, AttributeValue.builder().s(item.getEntity2()).build()
                    ))
                    .map(key -> DeleteRequest.builder().key(key).build())
                    .map(deleteRequest -> WriteRequest.builder().deleteRequest(deleteRequest).build())
                    .toList();

                batchWrite(0, requests);
            });
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
                        .attributeName(COLUMN_PK)
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
                        .attributeName(COLUMN_PK)
                        .keyType(KeyType.HASH)
                        .build(),
                    KeySchemaElement.builder()
                        .attributeName(COLUMN_SK)
                        .keyType(KeyType.RANGE)
                        .build()
                )
                .globalSecondaryIndexes(
                    GlobalSecondaryIndex.builder()
                        .indexName(GSI_PK_PARENT)
                        .keySchema(
                            KeySchemaElement.builder()
                                .attributeName(COLUMN_PK)
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
                        .indexName(GSI_PK_TYPE)
                        .keySchema(
                            KeySchemaElement.builder()
                                .attributeName(COLUMN_PK)
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
