package com.github.saphyra.apphub.service.feature.task_manager.domain.invitation.dao;

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
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_INVITED_BY;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_ORGANIZATION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_USER;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.GSI_INVITATION_INVITED_BY;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.GSI_INVITATION_ORGANIZATION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_ORGANIZATION;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.PREFIX_USER;

@Component
@Profile("!test")
@Slf4j
class InvitationRepository extends DynamoDbRepository {
    private final InvitationMapper invitationMapper;
    private final UuidConverter uuidConverter;

    InvitationRepository(TaskManagerDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, InvitationMapper invitationMapper, UuidConverter uuidConverter) {
        super(configuration.getInvitationTableName(), context);
        this.invitationMapper = invitationMapper;
        this.uuidConverter = uuidConverter;
    }

    void saveAll(List<Invitation> invitations) {
        List<WriteRequest> requests = invitations.stream()
            .map(invitation -> WriteRequest.builder()
                .putRequest(PutRequest.builder()
                    .item(invitationMapper.convertDomain(invitation))
                    .build())
                .build())
            .toList();

        batchWrite(requests, TaskManagerMonitoringFunctionality.SAVE_INVITATIONS);
    }

    List<Invitation> getByUserId(UUID userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#user = :user")
            .expressionAttributeNames(Map.of("#user", COLUMN_USER))
            .expressionAttributeValues(Map.of(":user", AttributeValue.builder().s(PREFIX_USER + uuidConverter.convertDomain(userId)).build()))
            .build();

        return query(request, TaskManagerMonitoringFunctionality.GET_INVITATIONS_OF_USER)
            .stream()
            .map(invitationMapper::convertEntity)
            .toList();
    }

    List<Invitation> getByInvitedUserIdAndOrganizationId(UUID userId, UUID organizationId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#user = :user AND #organization = :organization")
            .expressionAttributeNames(Map.of(
                "#user", COLUMN_USER,
                "#organization", COLUMN_ORGANIZATION
            ))
            .expressionAttributeValues(Map.of(
                ":user", AttributeValue.builder().s(PREFIX_USER + uuidConverter.convertDomain(userId)).build(),
                ":organization", AttributeValue.builder().s(PREFIX_ORGANIZATION + uuidConverter.convertDomain(organizationId)).build()
            ))
            .build();

        return query(request, TaskManagerMonitoringFunctionality.GET_INVITATIONS_BY_USER_ID_AND_ORGANIZATION_ID)
            .stream()
            .map(invitationMapper::convertEntity)
            .toList();
    }

    void delete(List<Invitation> invitations) {
        List<WriteRequest> requests = invitations.stream()
            .map(invitation -> WriteRequest.builder()
                .deleteRequest(DeleteRequest.builder()
                    .key(Map.of(
                        COLUMN_USER, AttributeValue.builder().s(PREFIX_USER + uuidConverter.convertDomain(invitation.getInvitedUserId())).build(),
                        COLUMN_ORGANIZATION, AttributeValue.builder().s(PREFIX_ORGANIZATION + uuidConverter.convertDomain(invitation.getOrganizationId())).build()
                    ))
                    .build())
                .build())
            .toList();

        batchWrite(requests, TaskManagerMonitoringFunctionality.DELETE_INVITATIONS);
    }

    List<Invitation> getByInvitedByUserId(UUID userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .indexName(GSI_INVITATION_INVITED_BY)
            .keyConditionExpression("#invitedBy = :invitedBy")
            .expressionAttributeNames(Map.of("#invitedBy", COLUMN_INVITED_BY))
            .expressionAttributeValues(Map.of(":invitedBy", AttributeValue.builder().s(PREFIX_USER + uuidConverter.convertDomain(userId)).build()))
            .build();

        return query(request, TaskManagerMonitoringFunctionality.GET_INVITATIONS_BY_INVITED_BY)
            .stream()
            .map(invitationMapper::convertEntity)
            .toList();
    }

    List<Invitation> getByOrganizationId(UUID organizationId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .indexName(GSI_INVITATION_ORGANIZATION)
            .keyConditionExpression("#organization = :organization")
            .expressionAttributeNames(Map.of("#organization", COLUMN_ORGANIZATION))
            .expressionAttributeValues(Map.of(":organization", AttributeValue.builder().s(PREFIX_ORGANIZATION + uuidConverter.convertDomain(organizationId)).build()))
            .build();

        return query(request, TaskManagerMonitoringFunctionality.GET_INVITATIONS_BY_ORGANIZATION_ID)
            .stream()
            .map(invitationMapper::convertEntity)
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
                        .attributeName(COLUMN_USER)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName(COLUMN_ORGANIZATION)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName(COLUMN_INVITED_BY)
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .globalSecondaryIndexes(
                    GlobalSecondaryIndex.builder()
                        .indexName(GSI_INVITATION_INVITED_BY)
                        .keySchema(
                            KeySchemaElement.builder()
                                .attributeName(COLUMN_INVITED_BY)
                                .keyType(KeyType.HASH)
                                .build()
                        )
                        .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                        .build(),
                    GlobalSecondaryIndex.builder()
                        .indexName(GSI_INVITATION_ORGANIZATION)
                        .keySchema(
                            KeySchemaElement.builder()
                                .attributeName(COLUMN_ORGANIZATION)
                                .keyType(KeyType.HASH)
                                .build()
                        )
                        .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                        .build()
                )
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName(COLUMN_USER)
                        .keyType(KeyType.HASH)
                        .build(),
                    KeySchemaElement.builder()
                        .attributeName(COLUMN_ORGANIZATION)
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
