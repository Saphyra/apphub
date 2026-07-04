package com.github.saphyra.apphub.integration.framework.db.dynamodb.notebook;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.db.dynamodb.DynamoDbUtil.getClient;

public class NotebookPinGroupDynamoDbRepository {
    private static final String COLUMN_PK = "pk";
    private static final String PREFIX_USER = "USER#";

    public static List<Map<String, AttributeValue>> getPinGroups(UUID userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(getNotebookPinGroupTableName())
            .keyConditionExpression("#pk = :pk")
            .expressionAttributeNames(Map.of("#pk", COLUMN_PK))
            .expressionAttributeValues(Map.of(":pk", AttributeValue.builder().s(PREFIX_USER + userId.toString()).build()))
            .build();

        return getClient()
            .query(request)
            .items();
    }

    private static String getNotebookPinGroupTableName() {
        return "apphub-%s-notebook-pin_group".formatted(TestConfiguration.ENVIRONMENT);
    }
}
