package com.github.saphyra.apphub.service.feature.calendar.common.dao;

import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@Getter
public class CalendarDynamoDbConfiguration implements DynamoDbRepositoryConfiguration {
    private final String tableName;
    private final int maxBatchRetryCount;
    private final long batchRetryDelayMs;

    CalendarDynamoDbConfiguration(
        @Value("${aws.dynamoDb.calendar.tableName}") String tableName,
        @Value("${aws.dynamoDb.maxBatchRetryCount}") int maxBatchRetryCount,
        @Value("${aws.dynamoDb.batchRetryDelayMs}") long batchRetryDelayMs
    ) {
        this.tableName = tableName;
        this.maxBatchRetryCount = maxBatchRetryCount;
        this.batchRetryDelayMs = batchRetryDelayMs;
    }
}
