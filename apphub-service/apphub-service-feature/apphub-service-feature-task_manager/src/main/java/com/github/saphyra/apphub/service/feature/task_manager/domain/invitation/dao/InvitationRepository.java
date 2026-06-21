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
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.task_manager.domain.TaskManagerConstants.COLUMN_SK;
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
            .keyConditionExpression("#pk = :pk")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":pk", AttributeValue.builder().s(PREFIX_USER + uuidConverter.convertDomain(userId)).build()))
            .build();

        return query(request, TaskManagerMonitoringFunctionality.GET_INVITATIONS_OF_USER)
            .stream()
            .map(invitationMapper::convertEntity)
            .toList();
    }

    List<Invitation> getByInvitedUserIdAndOrganizationId(UUID userId, UUID organizationId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :pk AND begins_with(#sk, :sk)")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":pk", AttributeValue.builder().s(PREFIX_USER + uuidConverter.convertDomain(userId)).build(),
                ":sk", AttributeValue.builder().s(PREFIX_ORGANIZATION + uuidConverter.convertDomain(organizationId)).build()
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
                        COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + uuidConverter.convertDomain(invitation.getInvitedUserId())).build(),
                        COLUMN_SK, AttributeValue.builder().s(
                                PREFIX_ORGANIZATION + uuidConverter.convertDomain(invitation.getOrganizationId())
                                    + "|"
                                    + PREFIX_USER + uuidConverter.convertDomain(invitation.getInvitedBy())
                            )
                            .build()
                    ))
                    .build())
                .build())
            .toList();

        batchWrite(requests, TaskManagerMonitoringFunctionality.DELETE_INVITATIONS);
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
