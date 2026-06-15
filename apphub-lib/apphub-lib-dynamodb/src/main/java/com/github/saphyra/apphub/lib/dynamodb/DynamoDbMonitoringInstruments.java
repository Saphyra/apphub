package com.github.saphyra.apphub.lib.dynamodb;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.lib.monitoring.core.MetricRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
class DynamoDbMonitoringInstruments {
    private static final String KEY_AVG_TRANSACTION_COUNT = "avgTransactionsPerOperation";
    private static final String KEY_MAX_TRANSACTION_COUNT = "maxTransactionsPerOperation";
    private static final String KEY_AVG_TRANSACTION_LATENCY = "avgTransactionLatency";
    private static final String KEY_MAX_TRANSACTION_LATENCY = "maxTransactionLatency";
    private static final String KEY_AVG_CONSUMED_CAPACITY = "avgConsumedCapacity";
    private static final String KEY_MAX_CONSUMED_CAPACITY = "maxConsumedCapacity";
    private static final String KEY_AVG_OPERATION_LATENCY = "avgOperationLatency";
    private static final String KEY_MAX_OPERATION_LATENCY = "maxOperationLatency";
    private static final String KEY_AVG_RECORD_COUNT = "avgRecordCount";
    private static final String KEY_MAX_RECORD_COUNT = "maxRecordCount";
    private static final String KEY_OPERATION_COUNT = "operationCount";
    private static final String KEY_TOTAL_RETRIED_RECORD_COUNT = "totalRetriedRecordCount";
    private static final String KEY_MAX_RETRIED_RECORD_COUNT = "maxRetriedRecordCount";
    private static final String KEY_MAX_RETRIED_RECORD_PER_TRANSACTION = "maxRetriedRecordPerTransaction";

    private final MetricRegistry metricRegistry;

    void reportPutItem(String functionality, long latency, Double consumedCapacity) {
        List<MetricPropertyModel> properties = new ArrayList<>();

        properties.add(createForOperationCount());
        properties.addAll(createForConsumedCapacity(consumedCapacity));
        properties.addAll(createForOperationLatency(latency));

        metricRegistry.reportMetric(Feature.DYNAMO_DB, functionality, properties);
    }

    void reportGetItem(String functionality, long latency, Double consumedCapacity) {
        List<MetricPropertyModel> properties = new ArrayList<>();

        properties.add(createForOperationCount());
        properties.addAll(createForConsumedCapacity(consumedCapacity));
        properties.addAll(createForOperationLatency(latency));

        metricRegistry.reportMetric(Feature.DYNAMO_DB, functionality, properties);
    }

    void reportDeleteItem(String functionality, long latency, Double consumedCapacity) {
        List<MetricPropertyModel> properties = new ArrayList<>();

        properties.add(createForOperationCount());
        properties.addAll(createForConsumedCapacity(consumedCapacity));
        properties.addAll(createForOperationLatency(latency));

        metricRegistry.reportMetric(Feature.DYNAMO_DB, functionality, properties);
    }


    void reportQuery(String functionality, int transactionCount, long totalTransactionLatency, long maxTransactionLatency, double consumedCapacity, long operationLatency, int recordCount) {
        List<MetricPropertyModel> properties = new ArrayList<>();

        properties.add(createForOperationCount());
        properties.addAll(createForTransactionCount(transactionCount));
        properties.addAll(createForTransactionLatency(transactionCount, totalTransactionLatency, maxTransactionLatency));
        properties.addAll(createForConsumedCapacity(consumedCapacity));
        properties.addAll(createForOperationLatency(operationLatency));
        properties.addAll(createForRecordCount(recordCount));

        metricRegistry.reportMetric(Feature.DYNAMO_DB, functionality, properties);
    }

    void reportScan(String functionality, int transactionCount, long totalTransactionLatency, long maxTransactionLatency, double consumedCapacity, long operationLatency, int recordCount) {
        List<MetricPropertyModel> properties = new ArrayList<>();

        properties.add(createForOperationCount());
        properties.addAll(createForTransactionCount(transactionCount));
        properties.addAll(createForTransactionLatency(transactionCount, totalTransactionLatency, maxTransactionLatency));
        properties.addAll(createForConsumedCapacity(consumedCapacity));
        properties.addAll(createForOperationLatency(operationLatency));
        properties.addAll(createForRecordCount(recordCount));

        metricRegistry.reportMetric(Feature.DYNAMO_DB, functionality, properties);
    }

    void reportBatchWrite(
        String functionality,
        long operationLatency,
        int transactionCount,
        long totalTransactionLatency,
        long maxTransactionLatency,
        double consumedCapacity,
        int retriedRecordCount,
        int maxRetriedRecordCount,
        int recordCount
    ) {
        List<MetricPropertyModel> properties = new ArrayList<>();

        properties.add(createForOperationCount());
        properties.addAll(createForTransactionCount(transactionCount));
        properties.addAll(createForTransactionLatency(transactionCount, totalTransactionLatency, maxTransactionLatency));
        properties.addAll(createForConsumedCapacity(consumedCapacity));
        properties.addAll(createForOperationLatency(operationLatency));
        properties.addAll(createForRecordCount(recordCount));
        properties.add(createForOperationCount());
        properties.addAll(createForRetriedRecordCount(retriedRecordCount, maxRetriedRecordCount));

        metricRegistry.reportMetric(Feature.DYNAMO_DB, functionality, properties);
    }

    void reportBatchGetItem(
        String functionality,
        long operationLatency,
        int transactionCount,
        long totalTransactionLatency,
        long maxTransactionLatency,
        double consumedCapacity,
        int retriedRecordCount,
        int maxRetriedRecordCount,
        int recordCount
    ) {
        List<MetricPropertyModel> properties = new ArrayList<>();

        properties.add(createForOperationCount());
        properties.addAll(createForTransactionCount(transactionCount));
        properties.addAll(createForTransactionLatency(transactionCount, totalTransactionLatency, maxTransactionLatency));
        properties.addAll(createForConsumedCapacity(consumedCapacity));
        properties.addAll(createForOperationLatency(operationLatency));
        properties.addAll(createForRecordCount(recordCount));
        properties.add(createForOperationCount());
        properties.addAll(createForRetriedRecordCount(retriedRecordCount, maxRetriedRecordCount));

        metricRegistry.reportMetric(Feature.DYNAMO_DB, functionality, properties);
    }

    private List<MetricPropertyModel> createForRetriedRecordCount(int retriedRecordCount, int maxRetriedRecordCount) {
        return List.of(
            MetricPropertyModel.builder()
                .key(KEY_TOTAL_RETRIED_RECORD_COUNT)
                .value((double) retriedRecordCount)
                .aggregationStrategy(AggregationStrategy.SUM)
                .build(),
            MetricPropertyModel.builder()
                .key(KEY_MAX_RETRIED_RECORD_COUNT)
                .value((double) retriedRecordCount)
                .aggregationStrategy(AggregationStrategy.MAX)
                .build(),
            MetricPropertyModel.builder()
                .key(KEY_MAX_RETRIED_RECORD_PER_TRANSACTION)
                .value((double) maxRetriedRecordCount)
                .aggregationStrategy(AggregationStrategy.MAX)
                .build()
        );
    }

    private MetricPropertyModel createForOperationCount() {
        return MetricPropertyModel.builder()
            .key(KEY_OPERATION_COUNT)
            .value(1.0)
            .aggregationStrategy(AggregationStrategy.SUM)
            .build();
    }

    private List<MetricPropertyModel> createForRecordCount(int recordCount) {
        return List.of(
            MetricPropertyModel.builder()
                .key(KEY_AVG_RECORD_COUNT)
                .value((double) recordCount)
                .aggregationStrategy(AggregationStrategy.AVERAGE)
                .build(),
            MetricPropertyModel.builder()
                .key(KEY_MAX_RECORD_COUNT)
                .value((double) recordCount)
                .aggregationStrategy(AggregationStrategy.MAX)
                .build()
        );
    }

    private List<MetricPropertyModel> createForOperationLatency(long operationLatency) {
        return List.of(
            MetricPropertyModel.builder()
                .key(KEY_AVG_OPERATION_LATENCY)
                .value((double) operationLatency)
                .aggregationStrategy(AggregationStrategy.AVERAGE)
                .build(),
            MetricPropertyModel.builder()
                .key(KEY_MAX_OPERATION_LATENCY)
                .value((double) operationLatency)
                .aggregationStrategy(AggregationStrategy.MAX)
                .build()
        );
    }

    private List<MetricPropertyModel> createForConsumedCapacity(double consumedCapacity) {
        return List.of(
            MetricPropertyModel.builder()
                .key(KEY_AVG_CONSUMED_CAPACITY)
                .value(consumedCapacity)
                .aggregationStrategy(AggregationStrategy.AVERAGE)
                .build(),
            MetricPropertyModel.builder()
                .key(KEY_MAX_CONSUMED_CAPACITY)
                .value(consumedCapacity)
                .aggregationStrategy(AggregationStrategy.MAX)
                .build()
        );
    }

    private List<MetricPropertyModel> createForTransactionLatency(int transactionCount, long totalTransactionLatency, long maxTransactionLatency) {
        return List.of(
            MetricPropertyModel.builder()
                .key(KEY_AVG_TRANSACTION_LATENCY)
                .value(transactionCount > 0 ? totalTransactionLatency / (double) transactionCount : 0)
                .aggregationStrategy(AggregationStrategy.AVERAGE)
                .build(),
            MetricPropertyModel.builder()
                .key(KEY_MAX_TRANSACTION_LATENCY)
                .value((double) maxTransactionLatency)
                .aggregationStrategy(AggregationStrategy.MAX)
                .build()
        );
    }

    private List<MetricPropertyModel> createForTransactionCount(int transactionCount) {
        return List.of(
            MetricPropertyModel.builder()
                .key(KEY_AVG_TRANSACTION_COUNT)
                .value((double) transactionCount)
                .aggregationStrategy(AggregationStrategy.AVERAGE)
                .build(),
            MetricPropertyModel.builder()
                .key(KEY_MAX_TRANSACTION_COUNT)
                .value((double) transactionCount)
                .aggregationStrategy(AggregationStrategy.MAX)
                .build()
        );
    }
}
