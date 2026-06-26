package com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.feature.task_manager.configuration.TaskManagerDynamoDbConfiguration;
import com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants;
import com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerMonitoringFunctionality;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_OBJECT;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_PRINCIPAL;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.GSI_ALM_OBJECT_PRINCIPAL;

@Component
@Profile("!test")
@Slf4j
class AlmRepository extends DynamoDbRepository {
    private final AlmMapper almMapper;
    private final UuidConverter uuidConverter;
    private final ExecutorServiceBean executorServiceBean;

    AlmRepository(TaskManagerDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, AlmMapper almMapper, UuidConverter uuidConverter, ExecutorServiceBean executorServiceBean) {
        super(configuration.getAlmTableName(), context);
        this.almMapper = almMapper;
        this.uuidConverter = uuidConverter;
        this.executorServiceBean = executorServiceBean;
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
            .keyConditionExpression("#principal = :principal AND begins_with(#object, :objectType)")
            .expressionAttributeNames(Map.of(
                "#principal", COLUMN_PRINCIPAL,
                "#object", COLUMN_OBJECT
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
                COLUMN_PRINCIPAL, AttributeValue.builder().s(principalType + "#" + uuidConverter.convertDomain(principalId)).build(),
                COLUMN_OBJECT, AttributeValue.builder().s(objectType + "#" + uuidConverter.convertDomain(objectId)).build()
            ))
            .build();


        return getItem(request, TaskManagerMonitoringFunctionality.FIND_ALM_FOR_OBJECT)
            .map(almMapper::convertEntity);
    }

    List<Alm> getByPrincipal(UUID userId, PrincipalType principalType) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#principal = :principal")
            .expressionAttributeNames(Map.of("#principal", COLUMN_PRINCIPAL))
            .expressionAttributeValues(Map.of(":principal", AttributeValue.builder().s(principalType.name() + "#" + uuidConverter.convertDomain(userId)).build()))
            .build();

        return query(request, TaskManagerMonitoringFunctionality.GET_ALMS_BY_PRINCIPAL)
            .stream()
            .map(almMapper::convertEntity)
            .toList();
    }

    List<Alm> getByObjects(List<BiWrapper<UUID, ObjectType>> keys) {
        List<FutureWrapper<List<Map<String, AttributeValue>>>> futures = keys.stream()
            .map(key -> QueryRequest.builder()
                .tableName(tableName)
                .indexName(TaskManagerConstants.GSI_ALM_OBJECT_PRINCIPAL)
                .keyConditionExpression("#object = :object")
                .expressionAttributeNames(Map.of("#object", COLUMN_OBJECT))
                .expressionAttributeValues(Map.of(":object", AttributeValue.builder().s(key.getEntity2() + "#" + uuidConverter.convertDomain(key.getEntity1())).build()))
                .build())
            .map(request -> executorServiceBean.asyncProcess(() -> query(request, TaskManagerMonitoringFunctionality.GET_ALMS_BY_OBJECTS)))
            .toList();

        return futures.stream()
            .map(FutureWrapper::get)
            .map(ExecutionResult::getOrThrow)
            .map(almMapper::convertEntity)
            .flatMap(List::stream)
            .toList();
    }

    void delete(List<Alm> alms) {
        List<WriteRequest> requests = alms.stream()
            .map(almMapper::convertDomain)
            .map(item -> WriteRequest.builder()
                .deleteRequest(DeleteRequest.builder()
                    .key(Map.of(
                        COLUMN_PRINCIPAL, item.get(COLUMN_PRINCIPAL),
                        COLUMN_OBJECT, item.get(COLUMN_OBJECT)
                    ))
                    .build())
                .build())
            .toList();

        batchWrite(requests, TaskManagerMonitoringFunctionality.DELETE_ALMS);
    }

    List<Alm> getForObject(UUID objectId, ObjectType objectType) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .indexName(GSI_ALM_OBJECT_PRINCIPAL)
            .keyConditionExpression("#object = :object")
            .expressionAttributeNames(Map.of("#object", COLUMN_OBJECT))
            .expressionAttributeValues(Map.of(":object", AttributeValue.builder().s(objectType + "#" + uuidConverter.convertDomain(objectId)).build()))
            .build();

        return query(request, TaskManagerMonitoringFunctionality.GET_ALMS_BY_OBJECT)
            .stream()
            .map(almMapper::convertEntity)
            .toList();
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
                        .attributeName(COLUMN_PRINCIPAL)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName(COLUMN_OBJECT)
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName(COLUMN_PRINCIPAL)
                        .keyType(KeyType.HASH)
                        .build(),
                    KeySchemaElement.builder()
                        .attributeName(COLUMN_OBJECT)
                        .keyType(KeyType.RANGE)
                        .build()
                )
                .globalSecondaryIndexes(GlobalSecondaryIndex.builder()
                    .indexName(TaskManagerConstants.GSI_ALM_OBJECT_PRINCIPAL)
                    .keySchema(
                        KeySchemaElement.builder()
                            .attributeName(COLUMN_OBJECT)
                            .keyType(KeyType.HASH)
                            .build(),
                        KeySchemaElement.builder()
                            .attributeName(COLUMN_PRINCIPAL)
                            .keyType(KeyType.RANGE)
                            .build()
                    )
                    .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                    .build())
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
