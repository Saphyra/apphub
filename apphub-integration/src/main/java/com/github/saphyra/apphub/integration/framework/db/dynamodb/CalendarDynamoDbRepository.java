package com.github.saphyra.apphub.integration.framework.db.dynamodb;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.db.dynamodb.DynamoDbUtil.getClient;

public class CalendarDynamoDbRepository {
    private static final String COLUMN_PK = "pk";
    private static final String COLUMN_PRINCIPAL = "principal";
    private static final String COLUMN_OBJECT = "object";
    private static final String PREFIX_USER = "USER#";
    private static final String PREFIX_EVENT = "EVENT#";
    private static final String PREFIX_ALM_USER = "USER#";

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

    public static boolean almRecordExists(UUID principal, SharedObjectType objectType, UUID objectId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(getCalendarAlmTableName())
            .key(Map.of(
                COLUMN_PRINCIPAL, AttributeValue.builder().s(PREFIX_ALM_USER + principal).build(),
                COLUMN_OBJECT, AttributeValue.builder().s(objectType + "#" + objectId).build()
            ))
            .build();

        return getClient()
            .getItem(request)
            .hasItem();
    }

    private static String getCalendarTableName() {
        return "apphub-%s-calendar".formatted(TestConfiguration.ENVIRONMENT);
    }

    private static String getCalendarAlmTableName() {
        return "apphub-calendar-%s-alm".formatted(TestConfiguration.ENVIRONMENT);
    }
}
