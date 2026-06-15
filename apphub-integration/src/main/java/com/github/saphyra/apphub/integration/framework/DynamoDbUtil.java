package com.github.saphyra.apphub.integration.framework;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
public class DynamoDbUtil {
    private static final String COLUMN_PK = "pk";
    private static final String COLUMN_SK = "sk";
    private static final String COLUMN_MARKED_FOR_DELETION_AT = "marked_for_deletion_at";
    private static final String COLUMN_LOCKED_UNTIL = "locked_until";

    private static final String TYPE_USER_ID = "USER_ID";
    private static final String TYPE_MARKED_FOR_DELETION = "MARKED_FOR_DELETION";
    private static final String TYPE_ROLE = "ROLE";
    private static final String TYPE_PROFILE = "PROFILE";
    private static final String TYPE_CREDENTIAL = "CREDENTIAL";
    private static final String COLUMN_USER_ID = "user_id";

    private static DynamoDbClient dynamoDbClient;

    public static void checkConnection() {
        getClient()
            .listTables();
    }

    public static List<String> getRolesByEmail(String email) {
        UUID userId = getUserIdByEmail(email);

        return getRolesByUserId(userId);
    }

    public static List<String> getRolesByUserId(UUID userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(getUserTableName())
            .keyConditionExpression("#pk = :userId AND begins_with(#sk, :role)")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":userId", AttributeValue.builder().s(String.join("#", TYPE_USER_ID, userId.toString())).build(),
                ":role", AttributeValue.builder().s(TYPE_ROLE).build()
            ))
            .build();

        return getClient()
            .query(request)
            .items()
            .stream()
            .map(record -> record.get(COLUMN_SK).s().split("#")[1])
            .toList();
    }

    public static void markForDeletionByEmail(String email) {
        UUID userId = getUserIdByEmail(email);
        markForDeletion(userId);
    }

    public static void unlockUserByEmail(String email) {
        UUID userId = getUserIdByEmail(email);

        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(getUserTableName())
            .keyConditionExpression("#pk = :value AND #sk = :profile")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":value", AttributeValue.builder().s(String.join("#", TYPE_USER_ID, userId.toString())).build(),
                ":profile", AttributeValue.builder().s(TYPE_PROFILE).build())
            )
            .build();

        QueryResponse queryResponse = getClient().query(queryRequest);

        Map<String, AttributeValue> profile = new HashMap<>(queryResponse.items().getFirst());
        profile.put(COLUMN_LOCKED_UNTIL, AttributeValue.builder().n("0").build());

        PutItemRequest putItemRequest = PutItemRequest.builder()
            .tableName(getUserTableName())
            .item(profile)
            .build();

        getClient().putItem(putItemRequest);
    }

    public static void removeRoleByEmail(String email, String role) {
        UUID userId = getUserIdByEmail(email);
        removeRole(userId, role);
    }

    private static void removeRole(UUID userId, String role) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(getUserTableName())
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(String.join("#", TYPE_USER_ID, userId.toString())).build(),
                COLUMN_SK, AttributeValue.builder().s(String.join("#", TYPE_ROLE, role)).build()
            ))
            .build();

        getClient().deleteItem(request);
    }

    public static void addRoleByEmail(String email, String role) {
        UUID userId = getUserIdByEmail(email);
        addRole(userId, role);
    }

    private static void addRole(UUID userId, String role) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(getUserTableName())
            .item(Map.of(
                COLUMN_PK, AttributeValue.builder().s(String.join("#", TYPE_USER_ID, userId.toString())).build(),
                COLUMN_SK, AttributeValue.builder().s(String.join("#", TYPE_ROLE, role)).build()
            ))
            .build();

        getClient().putItem(request);
    }

    public static UUID getUserIdByEmail(String email) {
        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(getUserTableName())
            .keyConditionExpression("#pk = :pk")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":pk", AttributeValue.builder().s(String.join("#", TYPE_CREDENTIAL, email)).build()))
            .build();

        QueryResponse queryResponse = getClient()
            .query(queryRequest);

        if (queryResponse.items().size() != 1) {
            throw new IllegalStateException("Expected exactly one user with email %s, but found %d".formatted(email, queryResponse.items().size()));
        }

        String userId = queryResponse.items()
            .getFirst()
            .get(COLUMN_USER_ID)
            .s();
        return UUID.fromString(userId.split("#")[1]);
    }

    public static void markForDeleteWhenEmailEndsWith(String prefix) {
        List<UUID> userIds = getUserIdsByEmailStartsWith(prefix);
        log.info("Deleting {} test users", userIds.size());
        userIds.forEach(DynamoDbUtil::markForDeletion);
    }

    public static void markForDeletion(UUID userId) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(getUserTableName())
            .item(Map.of(
                COLUMN_PK, AttributeValue.builder().s(String.join("#", TYPE_USER_ID, userId.toString())).build(),
                COLUMN_SK, AttributeValue.builder().s(TYPE_MARKED_FOR_DELETION).build(),
                COLUMN_MARKED_FOR_DELETION_AT, AttributeValue.builder().n(String.valueOf(0)).build()
            ))
            .build();

        getClient().putItem(request);
    }

    private static List<UUID> getUserIdsByEmailStartsWith(String prefix) {
        DynamoDbClient client = getClient();

        ScanRequest scanRequest = ScanRequest.builder()
            .tableName(getUserTableName())
            .filterExpression("begins_with(#pk, :prefix)")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":prefix", AttributeValue.builder().s(String.join("#", TYPE_CREDENTIAL, prefix)).build()))
            .build();

        ScanResponse scanResponse = client.scan(scanRequest);

        return scanResponse.items()
            .stream()
            .map(record -> record.get(COLUMN_USER_ID).s())
            .map(pk -> pk.split("#")[1])
            .distinct()
            .map(UUID::fromString)
            .toList();
    }

    public static int getRefreshTokenCountOfUser(UUID userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(getRefreshTokenTableName())
            .keyConditionExpression("#pk = :pk")
            .expressionAttributeNames(Map.of("#pk", "userId"))
            .expressionAttributeValues(Map.of(":pk", AttributeValue.builder().s(userId.toString()).build()))
            .build();

        return getClient()
            .query(request)
            .count();
    }

    private static String getRefreshTokenTableName() {
        return "apphub-%s-refresh_token".formatted(TestConfiguration.ENVIRONMENT);
    }

    private static String getUserTableName() {
        return "apphub-%s-user".formatted(TestConfiguration.ENVIRONMENT);
    }

    private synchronized static DynamoDbClient getClient() {
        if (isNull(dynamoDbClient)) {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(TestConfiguration.DYNAMO_DB_ACCESS_KEY_ID, TestConfiguration.DYNAMO_DB_SECRET_KEY);

            DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(Region.of(TestConfiguration.DYNAMO_DB_REGION))
                .credentialsProvider(StaticCredentialsProvider.create(credentials));

            if (!isBlank(TestConfiguration.DYNAMO_DB_HOST)) {
                builder = builder.endpointOverride(URI.create("http://" + TestConfiguration.DYNAMO_DB_HOST));
            }

            dynamoDbClient = builder.build();
        }

        return dynamoDbClient;
    }

    public static boolean profileExists(UUID userId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(getUserTableName())
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(String.join("#", TYPE_USER_ID, userId.toString())).build(),
                COLUMN_SK, AttributeValue.builder().s(TYPE_PROFILE).build()
            ))
            .build();

        return getClient()
            .getItem(request)
            .hasItem();
    }

    public static boolean credentialExists(String credential) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(getUserTableName())
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(String.join("#", TYPE_CREDENTIAL, credential)).build(),
                COLUMN_SK, AttributeValue.builder().s(TYPE_CREDENTIAL).build()
            ))
            .build();

        return getClient()
            .getItem(request)
            .hasItem();
    }

    public static boolean markedForDeletionExists(UUID userId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(getUserTableName())
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(String.join("#", TYPE_USER_ID, userId.toString())).build(),
                COLUMN_SK, AttributeValue.builder().s(TYPE_MARKED_FOR_DELETION).build()
            ))
            .build();

        return getClient()
            .getItem(request)
            .hasItem();
    }

    public static boolean calendarRecordExists(UUID userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(getCalendarTableName())
            .keyConditionExpression("#pk = :pk")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":pk", AttributeValue.builder().s("USER#" + userId.toString()).build()))
            .build();

        return !getClient()
            .query(request)
            .items()
            .isEmpty();
    }

    private static String getCalendarTableName() {
        return "apphub-%s-calendar".formatted(TestConfiguration.ENVIRONMENT);
    }

    public static boolean occurrenceExists(UUID eventId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(getCalendarTableName())
            .keyConditionExpression("#pk = :pk")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":pk", AttributeValue.builder().s("EVENT#" + eventId).build()))
            .build();

        return !getClient()
            .query(request)
            .items()
            .isEmpty();
    }

    public static boolean listItemExists(UUID userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(getListItemTableName())
            .keyConditionExpression("#pk = :pk")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":pk", AttributeValue.builder().s("USER#" + userId.toString()).build()))
            .build();

        return !getClient()
            .query(request)
            .items()
            .isEmpty();
    }

    private static String getListItemTableName() {
        return "apphub-%s-list_item".formatted(TestConfiguration.ENVIRONMENT);
    }

    public static boolean listItemHasChildren(UUID listItemId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(getListItemTableName())
            .keyConditionExpression("#pk = :pk")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":pk", AttributeValue.builder().s("LIST_ITEM#" + listItemId.toString()).build()))
            .build();

        return !getClient()
            .query(request)
            .items()
            .isEmpty();
    }
}
