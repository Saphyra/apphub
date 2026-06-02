package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDynamoDbConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_DATE_BUCKET;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.GSI_USER_ID_DATE_BUCKET;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_EVENT;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_OCCURRENCE;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
@Slf4j
class OccurrenceRepository extends DynamoDbRepository {
    private final DynamoDbClient client;
    private final String tableName;
    private final OccurrenceMapper mapper;
    private final int maxBatchRetryCount;
    private final long batchRetryDelayMs;
    private final SleepService sleepService;

    OccurrenceRepository(DynamoDbClient client, CalendarDynamoDbConfiguration configuration, OccurrenceMapper mapper, SleepService sleepService) {
        this.client = client;
        this.tableName = configuration.getTableName();
        this.mapper = mapper;
        this.sleepService = sleepService;
        this.maxBatchRetryCount = configuration.getMaxBatchRetryCount();
        this.batchRetryDelayMs = configuration.getBatchRetryDelayMs();
    }

    void save(OccurrenceEntity occurrence) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapper.convertDomain(occurrence))
            .build();

        client.putItem(request);
    }

    List<OccurrenceEntity> getByEventId(String eventId) {
        List<OccurrenceEntity> result = new ArrayList<>();
        Map<String, AttributeValue> lastKey;

        do {
            QueryRequest request = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("#pk = :eventId AND begins_with(#sk, :prefix)")
                .expressionAttributeNames(Map.of(
                    "#pk", COLUMN_PK,
                    "#sk", COLUMN_SK
                ))
                .expressionAttributeValues(Map.of(
                    ":eventId", AttributeValue.builder().s(PREFIX_EVENT + eventId).build(),
                    ":prefix", AttributeValue.builder().s(PREFIX_OCCURRENCE).build()
                ))
                .build();

            QueryResponse response = client.query(request);

            log.info("Returned {} Occurrences. lastKey: {}", response.items().size(), response.lastEvaluatedKey());

            response.items()
                .stream()
                .map(mapper::convertEntity)
                .forEach(result::add);

            lastKey = response.lastEvaluatedKey();
        } while (lastKey != null && !lastKey.isEmpty());

        return result;
    }

    void delete(String eventId, List<String> occurrenceIds) {
        if (occurrenceIds.size() > Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch size must be less than %d".formatted(Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE));
        }

        List<WriteRequest> requests = occurrenceIds.stream()
            .map(item -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_EVENT + eventId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_OCCURRENCE + item).build()
            ))
            .map(key -> DeleteRequest.builder().key(key).build())
            .map(deleteRequest -> WriteRequest.builder().deleteRequest(deleteRequest).build())
            .toList();

        batchWrite(0, requests);
    }

    void save(List<OccurrenceEntity> occurrences) {
        if (occurrences.size() > Constants.DYNAMO_DB_INSERT_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch size must be less than %d".formatted(Constants.DYNAMO_DB_INSERT_MAX_BATCH_SIZE));
        }

        List<WriteRequest> requests = occurrences.stream()
            .map(mapper::convertDomain)
            .map(item -> PutRequest.builder().item(item).build())
            .map(putRequest -> WriteRequest.builder().putRequest(putRequest).build())
            .toList();

        batchWrite(0, requests);
    }

    Optional<OccurrenceEntity> findById(String eventId, String occurrenceId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_EVENT + eventId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_OCCURRENCE + occurrenceId).build()
            ))
            .build();

        return Optional.of(client.getItem(request))
            .filter(GetItemResponse::hasItem)
            .map(GetItemResponse::item)
            .map(mapper::convertEntity);
    }

    //TODO handle lastEvaluatedKey
    public List<OccurrenceEntity> getByBucket(String userId, String bucket) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .indexName(GSI_USER_ID_DATE_BUCKET)
            .keyConditionExpression("#userId = :userId AND #dateBucket = :bucket")
            .expressionAttributeNames(Map.of(
                "#userId", COLUMN_USER_ID,
                "#dateBucket", COLUMN_DATE_BUCKET
            ))
            .expressionAttributeValues(Map.of(
                ":userId", AttributeValue.builder().s(PREFIX_USER + userId).build(),
                ":bucket", AttributeValue.builder().s(bucket).build()
            ))
            .build();

        return client.query(request)
            .items()
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    /**
     * @param occurrences List<BiWrapper<EventId, OccurrenceId>>
     */
    void delete(List<BiWrapper<String, String>> occurrences) {
        if (occurrences.size() > Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Batch size must be less than %d".formatted(Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE));
        }

        List<WriteRequest> requests = occurrences.stream()
            .map(item -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_EVENT + item.getEntity1()).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_OCCURRENCE + item.getEntity2()).build()
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
