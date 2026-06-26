package com.github.saphyra.apphub.service.notebook.dao.pin_group;

import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.notebook.config.NotebookDynamoDbConfiguration;
import com.github.saphyra.apphub.service.notebook.dao.NotebookMonitoringFunctionality;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDaoConstants.PREFIX_PIN_GROUP;
import static com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDaoConstants.PREFIX_USER;

@Component
@Slf4j
@Profile("!test")
class PinGroupRepository extends DynamoDbRepository {
    private final PinGroupMapper mapper;

    PinGroupRepository(NotebookDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, PinGroupMapper mapper) {
        super(configuration.getPinGroupTableName(), context);
        this.mapper = mapper;
    }

    void save(PinGroupEntity pinGroup) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapper.convertDomain(pinGroup))
            .build();

        putItem(request, NotebookMonitoringFunctionality.SAVE_PIN_GROUP);
    }


    Optional<PinGroupEntity> findById(String userId, String pinGroupId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_PIN_GROUP + pinGroupId).build()
            ))
            .build();

        return mapper.convertEntity(getItem(request, NotebookMonitoringFunctionality.FIND_PIN_GROUP));
    }

    List<PinGroupEntity> getByUserId(String userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :pk")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK
            ))
            .expressionAttributeValues(Map.of(
                ":pk", AttributeValue.builder().s(PREFIX_USER + userId).build()
            ))
            .build();

        return mapper.convertEntity(query(request, NotebookMonitoringFunctionality.GET_PIN_GROUPS_BY_USER_ID));
    }

    void delete(String userId, String pinGroupId) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_PIN_GROUP + pinGroupId).build()
            ))
            .build();

        deleteItem(request, NotebookMonitoringFunctionality.DELETE_PIN_GROUP);
    }

    void deleteByUserId(String userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :pk")
            .projectionExpression("#pk, #sk")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":pk", AttributeValue.builder().s(PREFIX_USER + userId).build()
            ))
            .build();

        List<Map<String, AttributeValue>> keys = query(request, NotebookMonitoringFunctionality.GET_PIN_GROUP_IDS_BY_USER_ID);

        List<WriteRequest> requests = keys.stream()
            .map(key -> WriteRequest.builder().deleteRequest(DeleteRequest.builder().key(key).build()).build())
            .toList();

        batchWrite(requests, NotebookMonitoringFunctionality.DELETE_PIN_GROUPS_BY_USER_ID);
    }

    void save(List<PinGroupEntity> pinGroups) {
        List<WriteRequest> requests = pinGroups.stream()
            .map(mapper::convertDomain)
            .map(entity -> WriteRequest.builder().putRequest(PutRequest.builder().item(entity).build()).build())
            .toList();

        batchWrite(requests, NotebookMonitoringFunctionality.SAVE_PIN_GROUPS);
    }

    @PostConstruct
    void createTable() {
        try {
            getClient()
                .describeTable(builder -> builder.tableName(tableName));
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
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

            getClient()
                .createTable(createTableRequest);

            getClient()
                .waiter()
                .waitUntilTableExists(builder -> builder.tableName(tableName));
        }
    }
}
