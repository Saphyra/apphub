package com.github.saphyra.apphub.integration.framework.db.dynamodb.task_manager;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.db.dynamodb.DynamoDbUtil.getClient;

public class TaskManagerInvitationDynamoDbRepository {
    private static final String COLUMN_USER = "user";
    private static final String PREFIX_USER = "USER#";

    public static List<Map<String, AttributeValue>> getInvitationsByUserId(UUID userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(getTaskManagerInvitationTable())
            .keyConditionExpression("#user = :user")
            .expressionAttributeNames(Map.of("#user", COLUMN_USER))
            .expressionAttributeValues(Map.of(":user", AttributeValue.builder().s(PREFIX_USER + userId).build()))
            .build();

        return getClient()
            .query(request)
            .items();
    }

    private static String getTaskManagerInvitationTable() {
        return "apphub-task_manager-%s-invitation".formatted(TestConfiguration.ENVIRONMENT);
    }
}
