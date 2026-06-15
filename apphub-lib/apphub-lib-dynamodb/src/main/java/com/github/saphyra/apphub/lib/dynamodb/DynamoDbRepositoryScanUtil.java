package com.github.saphyra.apphub.lib.dynamodb;

import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ReturnConsumedCapacity;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
class DynamoDbRepositoryScanUtil {
    private final DynamoDbClient client;
    private final DynamoDbMonitoringInstruments monitoringInstruments;

    List<Map<String, AttributeValue>> scan(ScanRequest scanRequest, String monitoringFunctionality) {
        scanRequest = scanRequest.toBuilder()
            .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
            .build();

        List<Map<String, AttributeValue>> result = new ArrayList<>();
        Map<String, AttributeValue> lastEvaluatedKey = null;

        Stopwatch operationStopwatch = Stopwatch.createStarted();
        int transactionCount = 0;
        long totalTransactionLatency = 0;
        long maxTransactionLatency = 0;
        double consumedCapacity = 0;
        do {
            transactionCount += 1;
            Stopwatch transactionStopwatch = Stopwatch.createStarted();
            scanRequest = scanRequest.toBuilder()
                .exclusiveStartKey(lastEvaluatedKey)
                .build();

            ScanResponse response = client.scan(scanRequest);
            consumedCapacity += response.consumedCapacity()
                .capacityUnits();

            result.addAll(response.items());

            lastEvaluatedKey = response.lastEvaluatedKey();

            transactionStopwatch.stop();
            long transactionLatency = transactionStopwatch.elapsed(TimeUnit.MILLISECONDS);
            totalTransactionLatency += transactionLatency;
            if (transactionLatency > maxTransactionLatency) {
                maxTransactionLatency = transactionLatency;
            }
        } while (nonNull(lastEvaluatedKey) && !lastEvaluatedKey.isEmpty());

        operationStopwatch.stop();

        long operationLatency = operationStopwatch.elapsed(TimeUnit.MILLISECONDS);
        int recordCount = result.size();

        monitoringInstruments.reportScan(
            monitoringFunctionality,
            transactionCount,
            totalTransactionLatency,
            maxTransactionLatency,
            consumedCapacity,
            operationLatency,
            recordCount
        );

        return result;
    }
}
