package com.github.saphyra.apphub.lib.dynamodb;

import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ReturnConsumedCapacity;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
class DynamoDbRepositoryDeleteItemUtil {
    private final DynamoDbClient client;
    private final DynamoDbMonitoringInstruments instruments;

    void deleteItem(String tableName, DeleteItemRequest request, String monitoringFunctionality) {
        request = request.toBuilder()
            .tableName(tableName)
            .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
            .build();

        Stopwatch stopwatch = Stopwatch.createStarted();
        DeleteItemResponse response = client.deleteItem(request);
        stopwatch.stop();

        instruments.reportDeleteItem(monitoringFunctionality, stopwatch.elapsed(TimeUnit.MILLISECONDS), response.consumedCapacity().capacityUnits());
    }
}
