package com.github.saphyra.apphub.service.user.data.dao.user;

import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.user.config.UserDynamoDbConfiguration;
import com.github.saphyra.apphub.service.user.config.properties.UserProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.COLUMN_EMAIL;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.COLUMN_LANGUAGE;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.COLUMN_LOCKED_UNTIL;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.COLUMN_MARKED_FOR_DELETION_AT;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.COLUMN_PASSWORD;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.COLUMN_PASSWORD_FAILURE_COUNT;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.COLUMN_USERNAME;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.GSI_MARKED_FOR_DELETION_AT;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.TYPE_CREDENTIAL;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.TYPE_MARKED_FOR_DELETION;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.TYPE_PROFILE;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.TYPE_ROLE;
import static com.github.saphyra.apphub.service.user.data.dao.user.UserDaoConstants.TYPE_USER_ID;

@Component
@Slf4j
@Profile("!test")
class UserRepository extends DynamoDbRepository {
    private final UserProperties userProperties;

    UserRepository(UserDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, UserProperties userProperties) {
        super(configuration.getTableName(), context);
        this.userProperties = userProperties;
    }

    public void deleteProfile(String userId) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(String.join("#", TYPE_USER_ID, userId)).build(),
                COLUMN_SK, AttributeValue.builder().s(TYPE_PROFILE).build()
            ))
            .build();

        client.deleteItem(request);
    }

    List<String> getUserIdsMarkedForDeletion(long currentTimeEpochSeconds) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .indexName(GSI_MARKED_FOR_DELETION_AT)
            .keyConditionExpression("#sk = :value AND #marked_for_deletion_at <= :current_time")
            .expressionAttributeNames(Map.of(
                "#sk", COLUMN_SK,
                "#marked_for_deletion_at", COLUMN_MARKED_FOR_DELETION_AT
            ))
            .expressionAttributeValues(Map.of(
                ":value", AttributeValue.builder().s(TYPE_MARKED_FOR_DELETION).build(),
                ":current_time", AttributeValue.builder().n(String.valueOf(currentTimeEpochSeconds)).build()
            ))
            .limit(userProperties.getDeleteAccountBatchCount())
            .build();

        return query(request)
            .stream()
            .map(record -> record.get(COLUMN_PK).s().split("#")[1])
            .toList();
    }

    void trySaveCredential(String userId, String credential) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(Map.of(
                COLUMN_PK, AttributeValue.builder().s(String.join("#", TYPE_CREDENTIAL, credential)).build(),
                COLUMN_SK, AttributeValue.builder().s(TYPE_CREDENTIAL).build(),
                COLUMN_USER_ID, AttributeValue.builder().s(String.join("#", TYPE_USER_ID, userId)).build()
            ))
            .conditionExpression("attribute_not_exists(#pk)")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .build();

        client.putItem(request);
    }

    void deleteCredential(String credential) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(String.join("#", TYPE_CREDENTIAL, credential)).build(),
                COLUMN_SK, AttributeValue.builder().s(TYPE_CREDENTIAL).build()
            ))
            .build();

        client.deleteItem(request);
    }

    void save(ProfileEntity profile) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(Map.of(
                COLUMN_PK, AttributeValue.builder().s(String.join("#", TYPE_USER_ID, profile.getUserId())).build(),
                COLUMN_SK, AttributeValue.builder().s(TYPE_PROFILE).build(),
                COLUMN_EMAIL, AttributeValue.builder().s(profile.getEmail()).build(),
                COLUMN_USERNAME, AttributeValue.builder().s(profile.getUsername()).build(),
                COLUMN_LANGUAGE, AttributeValue.builder().s(profile.getLanguage()).build(),
                COLUMN_PASSWORD, AttributeValue.builder().s(profile.getPassword()).build(),
                COLUMN_PASSWORD_FAILURE_COUNT, AttributeValue.builder().n(String.valueOf(profile.getPasswordFailureCount())).build(),
                COLUMN_LOCKED_UNTIL, AttributeValue.builder().n(String.valueOf(profile.getLockedUntil())).build()
            ))
            .build();

        client.putItem(request);
    }

    void addRole(String userId, String role) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(Map.of(
                COLUMN_PK, AttributeValue.builder().s(String.join("#", TYPE_USER_ID, userId)).build(),
                COLUMN_SK, AttributeValue.builder().s(String.join("#", TYPE_ROLE, role)).build()
            ))
            .build();

        client.putItem(request);
    }

    void markForDeletion(String userId, long markedForDeletionAtEpochSeconds) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(Map.of(
                COLUMN_PK, AttributeValue.builder().s(String.join("#", TYPE_USER_ID, userId)).build(),
                COLUMN_SK, AttributeValue.builder().s(TYPE_MARKED_FOR_DELETION).build(),
                COLUMN_MARKED_FOR_DELETION_AT, AttributeValue.builder().n(String.valueOf(markedForDeletionAtEpochSeconds)).build()
            ))
            .build();

        client.putItem(request);
    }

    void unmarkForDeletion(String userId) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(String.join("#", TYPE_USER_ID, userId)).build(),
                COLUMN_SK, AttributeValue.builder().s(TYPE_MARKED_FOR_DELETION).build()
            ))
            .build();

        client.deleteItem(request);
    }

    void deleteRole(String userId, String role) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(String.join("#", TYPE_USER_ID, userId)).build(),
                COLUMN_SK, AttributeValue.builder().s(String.join("#", TYPE_ROLE, role)).build()
            ))
            .build();

        client.deleteItem(request);
    }

    List<String> getAllUserIds() {
        ScanRequest scanRequest = ScanRequest.builder()
            .tableName(tableName)
            .filterExpression("#sk = :value")
            .expressionAttributeNames(Map.of("#sk", COLUMN_SK))
            .expressionAttributeValues(Map.of(":value", AttributeValue.builder().s(TYPE_PROFILE).build()))
            .build();

        return scan(scanRequest)
            .stream()
            .map(record -> record.get(COLUMN_PK).s().split("#")[1])
            .toList();
    }

    Optional<CredentialEntity> findByCredential(String query) {
        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :value")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":value", AttributeValue.builder().s(String.join("#", TYPE_CREDENTIAL, query)).build()))
            .build();

        QueryResponse queryResponse = client.query(queryRequest);

        if (queryResponse.items().size() > 1) {
            throw new IllegalStateException("Multiple credentials found for query " + query);
        }

        return queryResponse.items()
            .stream()
            .findFirst()
            .map(record -> new CredentialEntity(
                record.get(COLUMN_PK).s().split("#")[1],
                record.get(COLUMN_USER_ID).s().split("#")[1]
            ));
    }

    Optional<TriWrapper<ProfileEntity, List<String>, Optional<Long>>> findByUserId(String userId) {
        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :value")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":value", AttributeValue.builder().s(String.join("#", TYPE_USER_ID, userId)).build()))
            .build();

        QueryResponse queryResponse = client.query(queryRequest);

        if (queryResponse.items().isEmpty()) {
            return Optional.empty();
        }

        List<String> roles = new ArrayList<>();
        TriWrapper<ProfileEntity, List<String>, Optional<Long>> result = new TriWrapper<>(null, roles, Optional.empty());

        queryResponse.items()
            .forEach(record -> {
                String type = record.get(COLUMN_SK).s();

                if (TYPE_PROFILE.equals(type)) {
                    ProfileEntity profile = ProfileEntity.builder()
                        .userId(record.get(COLUMN_PK).s().split("#")[1])
                        .email(record.get(COLUMN_EMAIL).s())
                        .username(record.get(COLUMN_USERNAME).s())
                        .language(record.get(COLUMN_LANGUAGE).s())
                        .password(record.get(COLUMN_PASSWORD).s())
                        .passwordFailureCount(Integer.parseInt(record.get(COLUMN_PASSWORD_FAILURE_COUNT).n()))
                        .lockedUntil(Long.parseLong(record.get(COLUMN_LOCKED_UNTIL).n()))
                        .build();
                    result.setEntity1(profile);
                } else if (TYPE_MARKED_FOR_DELETION.equals(type)) {
                    Long markedForDeletionAt = Long.parseLong(record.get(COLUMN_MARKED_FOR_DELETION_AT).n());
                    result.setEntity3(Optional.of(markedForDeletionAt));
                } else if (type.startsWith(TYPE_ROLE)) {
                    String role = record.get(COLUMN_SK).s().split("#")[1];
                    roles.add(role);
                }
            });

        return Optional.of(result);
    }

    @PostConstruct
    void createTable() {
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
                        .attributeName(COLUMN_MARKED_FOR_DELETION_AT)
                        .attributeType(ScalarAttributeType.N)
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
                        .indexName(GSI_MARKED_FOR_DELETION_AT)
                        .keySchema(
                            KeySchemaElement.builder()
                                .attributeName(COLUMN_SK)
                                .keyType(KeyType.HASH)
                                .build(),
                            KeySchemaElement.builder()
                                .attributeName(COLUMN_MARKED_FOR_DELETION_AT)
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
