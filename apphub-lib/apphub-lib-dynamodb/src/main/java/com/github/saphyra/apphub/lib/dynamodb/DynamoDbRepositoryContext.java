package com.github.saphyra.apphub.lib.dynamodb;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Component
@RequiredArgsConstructor
@Getter
public class DynamoDbRepositoryContext {
    private final DynamoDbClient client;
    private final DynamoDbRepositoryQueryUtil queryUtil;
    private final DynamoDbRepositoryScanUtil scanUtil;
    private final DynamoDbRepositoryBatchWriteUtil batchWriteUtil;
    private final DynamoDbRepositoryBatchGetItemUtil batchGetItemUtil;
    private final DynamoDbRepositoryPutItemUtil putItemUtil;
    private final DynamoDbRepositoryGetItemUtil getItemUtil;
    private final DynamoDbRepositoryDeleteItemUtil deleteItemUtil;
}
