package com.github.saphyra.apphub.integration.framework.db.dynamodb;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.Map;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.db.dynamodb.DynamoDbUtil.getClient;

public class RefreshTokenDynamoDbRepository {
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
}
