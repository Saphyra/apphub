package com.github.saphyra.apphub.lib.dynamodb;

import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ReturnConsumedCapacity;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
class DynamoDbRepositoryGetItemUtil {
    private final DynamoDbClient client;
    private final DynamoDbMonitoringInstruments instruments;

    Optional<Map<String, AttributeValue>> getItem(String tableName, GetItemRequest request, String monitoringFunctionality) {
        request = request.toBuilder()
            .tableName(tableName)
            .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
            .build();

        Stopwatch stopwatch = Stopwatch.createStarted();
        GetItemResponse response = client.getItem(request);
        stopwatch.stop();

        instruments.reportGetItem(monitoringFunctionality, stopwatch.elapsed(TimeUnit.MILLISECONDS), response.consumedCapacity().capacityUnits());

        return Optional.of(response)
            .filter(GetItemResponse::hasItem)
            .map(GetItemResponse::item);
    }
}
