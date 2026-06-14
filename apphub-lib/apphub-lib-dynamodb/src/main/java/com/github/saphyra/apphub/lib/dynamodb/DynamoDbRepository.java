package com.github.saphyra.apphub.lib.dynamodb;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public abstract class DynamoDbRepository {
    protected final DynamoDbClient client;
    protected final int maxBatchRetryCount;
    protected final long batchRetryDelayMs;
    protected final SleepService sleepService;
    protected final String tableName;
    protected final ExecutorServiceBean executorServiceBean;

    protected DynamoDbRepository(String tableName, DynamoDbRepositoryContext context) {
        this.client = context.getClient();
        this.sleepService = context.getSleepService();

        DynamoDbRepositoryConfiguration configuration = context.getConfiguration();
        this.maxBatchRetryCount = configuration.getMaxBatchRetryCount();
        this.batchRetryDelayMs = configuration.getBatchRetryDelayMs();
        this.tableName = tableName;
        this.executorServiceBean = context.getExecutorServiceBean();
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
        if (requests.isEmpty()) {
            log.debug("Empty parameter list for batchWrite");
        } else if (requests.size() > Constants.DYNAMO_DB_WRITE_MAX_BATCH_SIZE) {
            List<FutureWrapper<Void>> futures = Lists.partition(requests, Constants.DYNAMO_DB_WRITE_MAX_BATCH_SIZE)
                .stream()
                .map(batch -> executorServiceBean.execute(() -> batchWrite(batch)))
                .toList();

            futures.stream()
                .map(FutureWrapper::get)
                .forEach(ExecutionResult::getOrThrow);
        } else {
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
    }

    protected List<Map<String, AttributeValue>> batchGetItem(List<Map<String, AttributeValue>> keys) {
        if (keys.isEmpty()) {
            return List.of();
        } else if (keys.size() > Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE) {
            List<FutureWrapper<List<Map<String, AttributeValue>>>> futures = Lists.partition(keys, Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE)
                .stream()
                .map(batch -> executorServiceBean.asyncProcess(() -> batchGetItem(batch)))
                .toList();

            return futures.stream()
                .map(FutureWrapper::get)
                .map(ExecutionResult::getOrThrow)
                .flatMap(List::stream)
                .toList();
        } else {
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
}
