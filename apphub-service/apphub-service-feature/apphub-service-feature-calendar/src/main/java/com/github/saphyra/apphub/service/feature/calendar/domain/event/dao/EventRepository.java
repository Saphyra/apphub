package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDynamoDbConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_EVENT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
class EventRepository extends DynamoDbRepository {
    private final DynamoDbClient client;
    private final EventMapper mapper;
    private final String tableName;
    private final SleepService sleepService;
    private final int maxBatchRetryCount;
    private final long batchRetryDelayMs;

    EventRepository(DynamoDbClient dynamoDbClient, EventMapper mapper, CalendarDynamoDbConfiguration configuration, SleepService sleepService) {
        this.client = dynamoDbClient;
        this.mapper = mapper;
        this.tableName = configuration.getTableName();
        this.sleepService = sleepService;
        this.maxBatchRetryCount = configuration.getMaxBatchRetryCount();
        this.batchRetryDelayMs = configuration.getBatchRetryDelayMs();
    }

    Optional<EventEntity> findById(String userId, String eventId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT + eventId).build()
            ))
            .build();

        return Optional.of(client.getItem(request))
            .filter(GetItemResponse::hasItem)
            .map(GetItemResponse::item)
            .map(mapper::convertEntity);
    }

    //TODO handle lastEvaluatedKey
    List<EventEntity> getByUserId(String userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :userId AND begins_with(#sk, :eventPrefix)")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":userId", AttributeValue.builder().s(PREFIX_USER + userId).build(),
                ":eventPrefix", AttributeValue.builder().s(PREFIX_EVENT).build()
            ))
            .build();

        return client.query(request)
            .items()
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    List<EventEntity> getByIds(List<BiWrapper<String, String>> ids) {
        if (ids.size() > Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch size cannot be greater than " + Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE);
        }

        List<Map<String, AttributeValue>> keys = ids.stream()
            .map(id -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + id.getEntity1()).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT + id.getEntity2()).build()
            ))
            .toList();

        BatchGetItemRequest request = BatchGetItemRequest.builder()
            .requestItems(Map.of(
                tableName,
                KeysAndAttributes.builder()
                    .keys(keys)
                    .build()
            ))
            .build();

        //TODO handle unprocessed keys
        return client.batchGetItem(request)
            .responses()
            .values()
            .stream()
            .flatMap(List::stream)
            .map(mapper::convertEntity)
            .toList();
    }

    void save(EventEntity eventEntity) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapper.convertDomain(eventEntity))
            .build();

        client.putItem(request);
    }

    void delete(String userId, List<String> eventIds) {
        if (eventIds.size() > Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch size must be less than %d".formatted(Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE));
        }

        List<WriteRequest> requests = eventIds.stream()
            .map(eventId -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT + eventId).build()
            ))
            .map(key -> DeleteRequest.builder().key(key).build())
            .map(deleteRequest -> WriteRequest.builder().deleteRequest(deleteRequest).build())
            .toList();

        batchWrite(0, requests);
    }

    private void batchWrite(int tryCount, List<WriteRequest> requests) {
        if (tryCount > maxBatchRetryCount) {
            throw ExceptionFactory.reportedException(HttpStatus.SERVICE_UNAVAILABLE, ErrorCode.GENERAL_ERROR, "Batch retry limit exceeded");
        }

        if (tryCount > 0) {
            sleepService.sleep(tryCount * batchRetryDelayMs);
        }

        BatchWriteItemRequest batchRequest = BatchWriteItemRequest.builder()
            .requestItems(Map.of(tableName, requests))
            .build();

        BatchWriteItemResponse response = client.batchWriteItem(batchRequest);

        if (response.hasUnprocessedItems()) {
            response.unprocessedItems()
                .values()
                .forEach(writeRequests -> batchWrite(tryCount + 1, writeRequests));
        }
    }
}
