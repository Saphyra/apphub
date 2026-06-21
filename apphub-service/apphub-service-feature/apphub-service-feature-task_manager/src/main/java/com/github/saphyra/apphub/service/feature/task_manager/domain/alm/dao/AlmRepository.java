package com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.feature.task_manager.configuration.TaskManagerDynamoDbConfiguration;
import com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerMonitoringFunctionality;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_SK;

@Component
@Profile("!test")
@Slf4j
class AlmRepository extends DynamoDbRepository {
    private final AlmMapper almMapper;
    private final UuidConverter uuidConverter;

    AlmRepository(TaskManagerDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, AlmMapper almMapper, UuidConverter uuidConverter) {
        super(configuration.getAlmTableName(), context);
        this.almMapper = almMapper;
        this.uuidConverter = uuidConverter;
    }

    void save(Alm alm) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(almMapper.convertDomain(alm))
            .build();

        putItem(request, TaskManagerMonitoringFunctionality.SAVE_ALM);
    }

    List<Alm> getByPrincipalAndObjectType(UUID principalId, PrincipalType principalType, ObjectType objectType) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :principal AND begins_with(#sk, :objectType)")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":principal", AttributeValue.builder().s(principalType.name() + "#" + uuidConverter.convertDomain(principalId)).build(),
                ":objectType", AttributeValue.builder().s(objectType.name()).build()
            ))
            .build();

        return query(request, TaskManagerMonitoringFunctionality.GET_ALM_BY_PRINCIPAL_AND_OBJECT_TYPE)
            .stream()
            .map(almMapper::convertEntity)
            .toList();
    }

    Optional<Alm> findForObject(UUID principalId, PrincipalType principalType, UUID objectId, ObjectType objectType) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(principalType + "#" + uuidConverter.convertDomain(principalId)).build(),
                COLUMN_SK, AttributeValue.builder().s(objectType + "#" + uuidConverter.convertDomain(objectId)).build()
            ))
            .build();


        return getItem(request, TaskManagerMonitoringFunctionality.FIND_ALM_FOR_OBJECT)
            .map(almMapper::convertEntity);
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
