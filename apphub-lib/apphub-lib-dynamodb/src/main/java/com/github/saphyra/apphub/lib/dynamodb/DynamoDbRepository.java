package com.github.saphyra.apphub.lib.dynamodb;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.nonNull;

public abstract class DynamoDbRepository {
    protected final DynamoDbClient client;
    protected final int maxBatchRetryCount;
    protected final long batchRetryDelayMs;
    protected final SleepService sleepService;
    protected final String tableName;

    protected DynamoDbRepository(String tableName, DynamoDbRepositoryContext context) {
        this.client = context.getClient();
        this.sleepService = context.getSleepService();

        DynamoDbRepositoryConfiguration configuration = context.getConfiguration();
        this.maxBatchRetryCount = configuration.getMaxBatchRetryCount();
        this.batchRetryDelayMs = configuration.getBatchRetryDelayMs();
        this.tableName = tableName;
    }

    protected List<Map<String, AttributeValue>> query(QueryRequest queryRequest) {
        List<Map<String, AttributeValue>> result = new ArrayList<>();
        Map<String, AttributeValue> lastEvaluatedKey = null;

        do {
            queryRequest = queryRequest.toBuilder()
                .exclusiveStartKey(lastEvaluatedKey)
                .build();

            QueryResponse response = client.query(queryRequest);

            result.addAll(response.items());

            lastEvaluatedKey = response.lastEvaluatedKey();
        } while (nonNull(lastEvaluatedKey) && !lastEvaluatedKey.isEmpty());

        return result;
    }

    protected List<Map<String, AttributeValue>> scan(ScanRequest scanRequest) {
        List<Map<String, AttributeValue>> result = new ArrayList<>();
        Map<String, AttributeValue> lastEvaluatedKey = null;

        do {
            scanRequest = scanRequest.toBuilder()
                .exclusiveStartKey(lastEvaluatedKey)
                .build();

            ScanResponse response = client.scan(scanRequest);

            result.addAll(response.items());

            lastEvaluatedKey = response.lastEvaluatedKey();
        } while (nonNull(lastEvaluatedKey) && !lastEvaluatedKey.isEmpty());

        return result;
    }

    protected void batchWrite(List<WriteRequest> requests) {
        if (requests.size() > Constants.DYNAMO_DB_WRITE_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Write batch size must not be greater than " + Constants.DYNAMO_DB_WRITE_MAX_BATCH_SIZE + ". It was: " + requests.size());
        }

        if (requests.isEmpty()) {
            return;
        }

        int tryCount = 0;
        List<WriteRequest> pendingRequests = requests;

        while (nonNull(pendingRequests) && !pendingRequests.isEmpty()) {
            if (tryCount > maxBatchRetryCount) {
                throw ExceptionFactory.reportedException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCode.GENERAL_ERROR, "Batch retry limit exceeded");
            }

            if (tryCount > 0) {
                sleepService.sleep(tryCount * batchRetryDelayMs);
            }

            BatchWriteItemRequest batchRequest = BatchWriteItemRequest.builder()
                .requestItems(Map.of(tableName, pendingRequests))
                .build();

            pendingRequests = client.batchWriteItem(batchRequest)
                .unprocessedItems()
                .get(tableName);

            tryCount++;
        }
    }

    protected List<Map<String, AttributeValue>> batchGetItem(List<Map<String, AttributeValue>> keys) {
        if (keys.size() > Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Get batch size must not be greater than " + Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE + ". It was: " + keys.size());
        }

        if (keys.isEmpty()) {
            return List.of();
        }

        int tryCount = 0;
        List<Map<String, AttributeValue>> pendingKeys = keys;
        Set<Map<String, AttributeValue>> result = new LinkedHashSet<>();

        while (!pendingKeys.isEmpty()) {
            if (tryCount > maxBatchRetryCount) {
                throw ExceptionFactory.reportedException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCode.GENERAL_ERROR, "Batch retry limit exceeded");
            }

            if (tryCount > 0) {
                sleepService.sleep(tryCount * batchRetryDelayMs);
            }

            BatchGetItemRequest request = BatchGetItemRequest.builder()
                .requestItems(Map.of(
                    tableName,
                    KeysAndAttributes.builder()
                        .keys(pendingKeys)
                        .build()
                ))
                .build();

            BatchGetItemResponse response = client.batchGetItem(request);

            result.addAll(response.responses().get(tableName));

            pendingKeys = response.unprocessedKeys()
                .values()
                .stream()
                .flatMap(keysAndAttributes -> keysAndAttributes.keys().stream())
                .toList();

            tryCount++;
        }

        return List.copyOf(result);
    }
}
