package com.github.saphyra.apphub.service.feature.task_manager.domain.notification.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
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
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.TimeToLiveSpecification;
import software.amazon.awssdk.services.dynamodb.model.UpdateTimeToLiveRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_ORGANIZATION_ID;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.GSI_NOTIFICATION_ORGANIZATION_ID_USER_ID;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_NOTIFICATION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_USER;

@Component
@Profile("!test")
@Slf4j
class NotificationRepository extends DynamoDbRepository {
    private final NotificationMapper mapper;

    NotificationRepository(TaskManagerDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, NotificationMapper mapper) {
        super(configuration.getNotificationTableName(), context);
        this.mapper = mapper;
    }

    void save(List<NotificationEntity> notifications) {
        List<WriteRequest> requests = notifications.stream()
            .map(notification -> WriteRequest.builder()
                .putRequest(PutRequest.builder()
                    .item(mapper.convertDomain(notification))
                    .build())
                .build())
            .toList();

        batchWrite(requests, TaskManagerMonitoringFunctionality.SAVE_NOTIFICATIONS);
    }

    List<NotificationEntity> getByUserId(String userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :pk")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":pk", AttributeValue.builder().s(PREFIX_USER + userId).build()))
            .build();

        return query(request, TaskManagerMonitoringFunctionality.GET_NOTIFICATIONS_BY_USER_ID)
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    void delete(List<BiWrapper<String, String>> ids) {
        List<WriteRequest> requests = ids.stream()
            .map(id -> WriteRequest.builder()
                .deleteRequest(DeleteRequest.builder()
                    .key(Map.of(
                        COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + id.getEntity1()).build(),
                        COLUMN_SK, AttributeValue.builder().s(PREFIX_NOTIFICATION + id.getEntity2()).build()
                    ))
                    .build())
                .build())
            .toList();

        batchWrite(requests, TaskManagerMonitoringFunctionality.DELETE_NOTIFICATIONS);
    }

    List<NotificationEntity> getByIds(List<BiWrapper<String, String>> ids) {
        List<Map<String, AttributeValue>> keys = ids.stream()
            .map(id -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + id.getEntity1()).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_NOTIFICATION + id.getEntity2()).build()
            ))
            .toList();

        return batchGetItem(keys, TaskManagerMonitoringFunctionality.GET_NOTIFICATIONS)
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    List<NotificationEntity> getByUserIdAndOrganizationId(String userId, String organizationId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .indexName(GSI_NOTIFICATION_ORGANIZATION_ID_USER_ID)
            .keyConditionExpression("#sk = :sk AND #pk = :pk")
            .expressionAttributeNames(Map.of(
                "#sk", COLUMN_SK,
                "#pk", COLUMN_PK
            ))
            .expressionAttributeValues(Map.of(
                ":sk", AttributeValue.builder().s(PREFIX_NOTIFICATION + organizationId).build(),
                ":pk", AttributeValue.builder().s(PREFIX_USER + userId).build()
            ))
            .build();

        return query(request, TaskManagerMonitoringFunctionality.GET_NOTIFICATIONS_BY_USER_ID_AND_ORGANIZATION_ID)
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    List<NotificationEntity> getByOrganizationId(String organizationId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .indexName(GSI_NOTIFICATION_ORGANIZATION_ID_USER_ID)
            .keyConditionExpression("#sk = :sk")
            .expressionAttributeNames(Map.of("#sk", COLUMN_SK))
            .expressionAttributeValues(Map.of(":sk", AttributeValue.builder().s(PREFIX_NOTIFICATION + organizationId).build()))
            .build();

        return query(request, TaskManagerMonitoringFunctionality.GET_NOTIFICATIONS_BY_ORGANIZATION_ID)
            .stream()
            .map(mapper::convertEntity)
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
                        .attributeName(COLUMN_PK)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName(COLUMN_SK)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName(COLUMN_ORGANIZATION_ID)
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
                .globalSecondaryIndexes(GlobalSecondaryIndex.builder()
                    .indexName(TaskManagerConstants.GSI_NOTIFICATION_ORGANIZATION_ID_USER_ID)
                    .keySchema(
                        KeySchemaElement.builder()
                            .attributeName(COLUMN_ORGANIZATION_ID)
                            .keyType(KeyType.HASH)
                            .build(),
                        KeySchemaElement.builder()
                            .attributeName(COLUMN_PK)
                            .keyType(KeyType.RANGE)
                            .build()
                    )
                    .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                    .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

            getClient()
                .createTable(createTableRequest);

            UpdateTimeToLiveRequest request = UpdateTimeToLiveRequest.builder()
                .tableName(tableName)
                .timeToLiveSpecification(TimeToLiveSpecification.builder()
                    .attributeName("expiration")
                    .enabled(true)
                    .build())
                .build();

            getClient()
                .updateTimeToLive(request);

            getClient()
                .waiter()
                .waitUntilTableExists(builder -> builder.tableName(tableName));
        }
    }
}
