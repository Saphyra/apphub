package com.github.saphyra.apphub.integration.framework.db.dynamodb.notebook;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.db.dynamodb.DynamoDbUtil.getClient;

public class NotebookListItemDynamoDbRepository {
    private static final String COLUMN_PK = "pk";
    private static final String PREFIX_USER = "USER#";
    private static final String PREFIX_LIST_ITEM = "LIST_ITEM#";

    public static boolean listItemExists(UUID userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(getListItemTableName())
            .keyConditionExpression("#pk = :pk")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":pk", AttributeValue.builder().s(PREFIX_USER + userId.toString()).build()))
            .build();

        return !getClient()
            .query(request)
            .items()
            .isEmpty();
    }

    public static boolean listItemHasChildren(UUID listItemId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(getListItemTableName())
            .keyConditionExpression("#pk = :pk")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":pk", AttributeValue.builder().s(PREFIX_LIST_ITEM + listItemId.toString()).build()))
            .build();

        return !getClient()
            .query(request)
            .items()
            .isEmpty();
    }

    private static String getListItemTableName() {
        return "apphub-%s-list_item".formatted(TestConfiguration.ENVIRONMENT);
    }
}
