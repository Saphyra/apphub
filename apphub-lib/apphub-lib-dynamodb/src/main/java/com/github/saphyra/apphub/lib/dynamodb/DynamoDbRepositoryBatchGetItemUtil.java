package com.github.saphyra.apphub.lib.dynamodb;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ConsumedCapacity;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;
import software.amazon.awssdk.services.dynamodb.model.ReturnConsumedCapacity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
class DynamoDbRepositoryBatchGetItemUtil {
    private final ExecutorServiceBean executorServiceBean;
    private final DynamoDbRepositoryConfiguration configuration;
    private final SleepService sleepService;
    private final DynamoDbClient client;
    private final DynamoDbMonitoringInstruments dynamoDbMonitoringInstruments;

    List<Map<String, AttributeValue>> batchGetItem(String tableName, List<Map<String, AttributeValue>> keys, String monitoringFunctionality) {
        if (keys.isEmpty()) {
            return List.of();
        }

        Stopwatch operationStopwatch = Stopwatch.createStarted();
        List<FutureWrapper<BiWrapper<List<Map<String, AttributeValue>>, TransactionMetrics>>> futures = Lists.partition(keys, Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE)
            .stream()
            .map(batch -> executorServiceBean.asyncProcess(() -> batchGetItem(tableName, batch)))
            .toList();

        List<BiWrapper<List<Map<String, AttributeValue>>, TransactionMetrics>> results = futures.stream()
            .map(FutureWrapper::get)
            .map(ExecutionResult::getOrThrow)
            .toList();
        operationStopwatch.stop();

        List<Map<String, AttributeValue>> result = new ArrayList<>();

        long operationLatency = operationStopwatch.elapsed(TimeUnit.MILLISECONDS);
        int transactionCount = 0;
        long totalTransactionLatency = 0;
        long maxTransactionLatency = 0;
        double consumedCapacity = 0;
        int retriedRecordCount = 0;
        int maxRetriedRecordCount = 0;
        for (BiWrapper<List<Map<String, AttributeValue>>, TransactionMetrics> bw : results) {
            result.addAll(bw.getEntity1());
            TransactionMetrics transactionMetrics = bw.getEntity2();

            transactionCount += transactionMetrics.transactionCount();
            totalTransactionLatency += transactionMetrics.totalTransactionLatency();
            maxTransactionLatency = Math.max(maxTransactionLatency, transactionMetrics.maxTransactionLatency());
            consumedCapacity += transactionMetrics.consumedCapacity();
            retriedRecordCount += transactionMetrics.totalRetriedRecordCount();
            maxRetriedRecordCount = Math.max(maxRetriedRecordCount, transactionMetrics.maxRetriedRecordCount());
        }

        dynamoDbMonitoringInstruments.reportBatchGetItem(
            monitoringFunctionality,
            operationLatency,
            transactionCount,
            totalTransactionLatency,
            maxTransactionLatency,
            consumedCapacity,
            retriedRecordCount,
            maxRetriedRecordCount,
            result.size()
        );

        return result;
    }

    private BiWrapper<List<Map<String, AttributeValue>>, TransactionMetrics> batchGetItem(String tableName, List<Map<String, AttributeValue>> batch) {
        long totalTransactionLatency = 0;
        long maxTransactionLatency = 0;
        double consumedCapacity = 0;
        int totalRetriedRecordCount = 0;
        int maxRetriedRecordCount = 0;
        int transactionCount = 0;

        List<Map<String, AttributeValue>> pendingKeys = batch;
        Set<Map<String, AttributeValue>> result = new LinkedHashSet<>();

        while (!pendingKeys.isEmpty()) {
            if (transactionCount > configuration.getMaxBatchRetryCount()) {
                throw ExceptionFactory.reportedException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCode.GENERAL_ERROR, "Batch retry limit exceeded");
            }

            if (transactionCount > 0) {
                sleepService.sleep(transactionCount * configuration.getBatchRetryDelayMs());
            }

            BatchGetItemRequest request = BatchGetItemRequest.builder()
                .requestItems(Map.of(
                    tableName,
                    KeysAndAttributes.builder()
                        .keys(pendingKeys)
                        .build()
                ))
                .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
                .build();

            Stopwatch transactionStopwatch = Stopwatch.createStarted();
            BatchGetItemResponse response = client.batchGetItem(request);
            transactionStopwatch.stop();

            result.addAll(response.responses().get(tableName));

            pendingKeys = response.unprocessedKeys()
                .values()
                .stream()
                .flatMap(keysAndAttributes -> keysAndAttributes.keys().stream())
                .toList();

            //Monitoring

            consumedCapacity += response.consumedCapacity()
                .stream()
                .mapToDouble(ConsumedCapacity::capacityUnits)
                .sum();
            long transactionLatency = transactionStopwatch.elapsed(TimeUnit.MILLISECONDS);
            totalTransactionLatency += transactionLatency;
            if (transactionLatency > maxTransactionLatency) {
                maxTransactionLatency = transactionLatency;
            }

            int retriedRecordCount = pendingKeys.size();
            totalRetriedRecordCount += retriedRecordCount;
            if (retriedRecordCount > maxRetriedRecordCount) {
                maxRetriedRecordCount = retriedRecordCount;
            }

            transactionCount++;
        }

        return new BiWrapper<>(
            List.copyOf(result),
            new TransactionMetrics(transactionCount, totalTransactionLatency, maxTransactionLatency, consumedCapacity, totalRetriedRecordCount, maxRetriedRecordCount)
        );
    }

    private record TransactionMetrics(int transactionCount, long totalTransactionLatency, long maxTransactionLatency, double consumedCapacity, int totalRetriedRecordCount, int maxRetriedRecordCount) {
    }
}
