package com.github.saphyra.apphub.lib.dynamodb;

public interface DynamoDbRepositoryConfiguration {
    int getMaxBatchRetryCount();

    long getBatchRetryDelayMs();
}
