package com.github.saphyra.apphub.lib.dynamodb;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ConsumedCapacity;
import software.amazon.awssdk.services.dynamodb.model.ReturnConsumedCapacity;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Component
@Slf4j
class DynamoDbRepositoryBatchWriteUtil {
    private final ExecutorServiceBean executorServiceBean;
    private final SleepService sleepService;
    private final DynamoDbRepositoryConfiguration configuration;
    private final DynamoDbClient client;
    private final DynamoDbMonitoringInstruments dynamoDbMonitoringInstruments;

    protected void batchWrite(String tableName, List<WriteRequest> requests, String monitoringFunctionality) {
        if (requests.isEmpty()) {
            return;
        }

        Stopwatch operationStopwatch = Stopwatch.createStarted();
        List<FutureWrapper<TransactionMetrics>> futures = Lists.partition(requests, Constants.DYNAMO_DB_WRITE_MAX_BATCH_SIZE)
            .stream()
            .map(batch -> executorServiceBean.asyncProcess(() -> this.batchWrite(tableName, batch)))
            .toList();

        List<TransactionMetrics> transactionMetrics = futures.stream()
            .map(FutureWrapper::get)
            .map(ExecutionResult::getOrThrow)
            .toList();

        operationStopwatch.stop();
        long operationLatency = operationStopwatch.elapsed(TimeUnit.MILLISECONDS);
        int transactionCount = 0;
        long totalTransactionLatency = 0;
        long maxTransactionLatency = 0;
        double consumedCapacity = 0;
        int retriedRecordCount = 0;
        int maxRetriedRecordCount = 0;
        for (TransactionMetrics transactionMetric : transactionMetrics) {
            transactionCount += transactionMetric.transactionCount();
            totalTransactionLatency += transactionMetric.totalTransactionLatency();
            maxTransactionLatency = Math.max(maxTransactionLatency, transactionMetric.maxTransactionLatency());
            consumedCapacity += transactionMetric.consumedCapacity();
            retriedRecordCount += transactionMetric.totalRetriedRecordCount();
            maxRetriedRecordCount = Math.max(maxRetriedRecordCount, transactionMetric.maxRetriedRecordCount());
        }

        dynamoDbMonitoringInstruments.reportBatchWrite(
            monitoringFunctionality,
            operationLatency,
            transactionCount,
            totalTransactionLatency,
            maxTransactionLatency,
            consumedCapacity,
            retriedRecordCount,
            maxRetriedRecordCount,
            requests.size()
        );
    }

    private TransactionMetrics batchWrite(String tableName, List<WriteRequest> requests) {
        int transactionCount = 0;
        long totalTransactionLatency = 0;
        long maxTransactionLatency = 0;
        double consumedCapacity = 0;
        int totalRetriedRecordCount = 0;
        int maxRetriedRecordCount = 0;

        List<WriteRequest> pendingRequests = requests;

        while (nonNull(pendingRequests) && !pendingRequests.isEmpty()) {
            if (transactionCount > configuration.getMaxBatchRetryCount()) {
                throw ExceptionFactory.reportedException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCode.GENERAL_ERROR, "Batch retry limit exceeded");
            }

            if (transactionCount > 0) {
                sleepService.sleep(transactionCount * configuration.getBatchRetryDelayMs());
            }

            BatchWriteItemRequest batchRequest = BatchWriteItemRequest.builder()
                .requestItems(Map.of(tableName, pendingRequests))
                .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
                .build();

            Stopwatch transactionStopwatch = Stopwatch.createStarted();
            BatchWriteItemResponse response = client.batchWriteItem(batchRequest);
            transactionStopwatch.stop();

            pendingRequests = response.unprocessedItems()
                .get(tableName);

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

            int retriedRecordCount = Optional.ofNullable(pendingRequests).map(List::size).orElse(0);
            totalRetriedRecordCount += retriedRecordCount;
            if (retriedRecordCount > maxRetriedRecordCount) {
                maxRetriedRecordCount = retriedRecordCount;
            }

            transactionCount++;
        }

        return new TransactionMetrics(transactionCount, totalTransactionLatency, maxTransactionLatency, consumedCapacity, totalRetriedRecordCount, maxRetriedRecordCount);
    }

    private record TransactionMetrics(int transactionCount, long totalTransactionLatency, long maxTransactionLatency, double consumedCapacity, int totalRetriedRecordCount, int maxRetriedRecordCount) {
    }
}
