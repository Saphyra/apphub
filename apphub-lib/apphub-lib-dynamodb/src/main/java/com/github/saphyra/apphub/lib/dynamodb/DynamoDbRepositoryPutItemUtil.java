package com.github.saphyra.apphub.lib.dynamodb;

import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ReturnConsumedCapacity;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
class DynamoDbRepositoryPutItemUtil {
    private final DynamoDbClient client;
    private final DynamoDbMonitoringInstruments instruments;

    void putItem(String tableName, PutItemRequest request, String monitoringFunctionality) {
        request = request.toBuilder()
            .tableName(tableName)
            .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
            .build();

        Stopwatch stopwatch = Stopwatch.createStarted();
        PutItemResponse response = client.putItem(request);
        stopwatch.stop();

        instruments.reportPutItem(monitoringFunctionality, stopwatch.elapsed(TimeUnit.MILLISECONDS), response.consumedCapacity().capacityUnits());
    }
}
