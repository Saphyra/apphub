package com.github.saphyra.apphub.service.notebook.config;

import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@Getter
public class NotebookDynamoDbConfiguration implements DynamoDbRepositoryConfiguration {
    private final String listItemTableName;
    private final String pinGroupTableName;
    private final int maxBatchRetryCount;
    private final long batchRetryDelayMs;

    NotebookDynamoDbConfiguration(
        @Value("${aws.dynamoDb.listItem.tableName}") String listItemTableName,
        @Value("${aws.dynamoDb.pinGroup.tableName}") String pinGroupTableName,
        @Value("${aws.dynamoDb.maxBatchRetryCount}") int maxBatchRetryCount,
        @Value("${aws.dynamoDb.batchRetryDelayMs}") long batchRetryDelayMs
    ) {
        this.listItemTableName = listItemTableName;
        this.pinGroupTableName = pinGroupTableName;
        this.maxBatchRetryCount = maxBatchRetryCount;
        this.batchRetryDelayMs = batchRetryDelayMs;
    }
}
