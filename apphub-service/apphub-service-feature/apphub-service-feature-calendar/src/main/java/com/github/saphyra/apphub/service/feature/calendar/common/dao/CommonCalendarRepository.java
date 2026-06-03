package com.github.saphyra.apphub.service.feature.calendar.common.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
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

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_DATE_BUCKET;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.GSI_USER_ID_DATE_BUCKET;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
@Slf4j
@Profile("!test")
class CommonCalendarRepository extends DynamoDbRepository {
    CommonCalendarRepository(CalendarDynamoDbConfiguration configuration, DynamoDbRepositoryContext context) {
        super(configuration.getTableName(), context);
    }

    void deleteByUserId(String userId) {
        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :userId")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":userId", AttributeValue.builder().s(PREFIX_USER + userId).build()))
            .build();

        List<BiWrapper<String, String>> items = query(queryRequest)
            .stream()
            .map(map -> new BiWrapper<>(
                map.get(COLUMN_PK).s(),
                map.get(COLUMN_SK).s()
            ))
            .toList();

        Lists.partition(items, Constants.DYNAMO_DB_WRITE_MAX_BATCH_SIZE)
            .forEach(batch -> {
                List<WriteRequest> requests = batch.stream()
                    .map(item -> Map.of(
                        COLUMN_PK, AttributeValue.builder().s(item.getEntity1()).build(),
                        COLUMN_SK, AttributeValue.builder().s(item.getEntity2()).build()
                    ))
                    .map(key -> DeleteRequest.builder().key(key).build())
                    .map(deleteRequest -> WriteRequest.builder().deleteRequest(deleteRequest).build())
                    .toList();

                batchWrite(requests);
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
                        .attributeName(COLUMN_PK)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName(COLUMN_SK)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName(COLUMN_USER_ID)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName(COLUMN_DATE_BUCKET)
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
                        .indexName(GSI_USER_ID_DATE_BUCKET)
                        .keySchema(
                            KeySchemaElement.builder()
                                .attributeName(COLUMN_USER_ID)
                                .keyType(KeyType.HASH)
                                .build(),
                            KeySchemaElement.builder()
                                .attributeName(COLUMN_DATE_BUCKET)
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
