package com.github.saphyra.apphub.service.feature.task_manager.configuration;

import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@Getter
public class TaskManagerDynamoDbConfiguration implements DynamoDbRepositoryConfiguration {
    private final int maxBatchRetryCount;
    private final long batchRetryDelayMs;
    private final String almTableName;
    private final String invitationTableName;
    private final String notificationTableName;
    private final String organizationTableName;

    TaskManagerDynamoDbConfiguration(
        @Value("${aws.dynamoDb.maxBatchRetryCount}") int maxBatchRetryCount,
        @Value("${aws.dynamoDb.batchRetryDelayMs}") long batchRetryDelayMs,
        @Value("${aws.dynamoDb.alm.tableName}") String almTableName,
        @Value("${aws.dynamoDb.notification.tableName}") String notificationTableName,
        @Value("${aws.dynamoDb.organization.tableName}") String organizationTableName,
        @Value("${aws.dynamoDb.invitation.tableName}") String invitationTableName
    ) {
        this.maxBatchRetryCount = maxBatchRetryCount;
        this.batchRetryDelayMs = batchRetryDelayMs;
        this.almTableName = almTableName;
        this.invitationTableName = invitationTableName;
        this.notificationTableName = notificationTableName;
        this.organizationTableName = organizationTableName;
    }
}
