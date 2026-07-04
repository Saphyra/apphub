package com.github.saphyra.apphub.integration.framework.db.dynamodb;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.db.dynamodb.DynamoDbUtil.getClient;

public class CalendarDynamoDbRepository {
    private static final String COLUMN_PK = "pk";
    private static final String PREFIX_USER = "USER#";
    private static final String PREFIX_EVENT = "EVENT#";

    public static boolean calendarRecordExists(UUID userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(getCalendarTableName())
            .keyConditionExpression("#pk = :pk")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":pk", AttributeValue.builder().s(PREFIX_USER + userId.toString()).build()))
            .build();

        return !getClient()
            .query(request)
            .items()
            .isEmpty();
    }

    public static boolean occurrenceExists(UUID eventId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(getCalendarTableName())
            .keyConditionExpression("#pk = :pk")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":pk", AttributeValue.builder().s(PREFIX_EVENT + eventId).build()))
            .build();

        return !getClient()
            .query(request)
            .items()
            .isEmpty();
    }

    private static String getCalendarTableName() {
        return "apphub-%s-calendar".formatted(TestConfiguration.ENVIRONMENT);
    }
}
