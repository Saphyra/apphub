package com.github.saphyra.apphub.integration.framework.db.dynamodb.task_manager;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.db.dynamodb.DynamoDbUtil.getClient;

public class TaskManagerOrganizationDynamoDbRepository {
    private static final String COLUMN_ORGANIZATION = "organization";
    private static final String PREFIX_ORGANIZATION = "ORGANIZATION#";

    public static Optional<Map<String, AttributeValue>> findOrganization(UUID organizationId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(getTaskManagerOrganizationTableName())
            .key(Map.of(COLUMN_ORGANIZATION, AttributeValue.builder().s(PREFIX_ORGANIZATION + organizationId.toString()).build()))
            .build();

        return Optional.of(getClient())
            .map(client -> client.getItem(request))
            .filter(GetItemResponse::hasItem)
            .map(GetItemResponse::item);
    }

    private static String getTaskManagerOrganizationTableName() {
        return "apphub-task_manager-%s-organization".formatted(TestConfiguration.ENVIRONMENT);
    }
}
