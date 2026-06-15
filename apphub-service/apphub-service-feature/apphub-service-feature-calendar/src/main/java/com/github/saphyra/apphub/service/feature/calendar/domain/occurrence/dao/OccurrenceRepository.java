package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDynamoDbConfiguration;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarMonitoringFunctionality;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

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
    private final OccurrenceMapper mapper;

    OccurrenceRepository(CalendarDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, OccurrenceMapper mapper) {
        super(configuration.getTableName(), context);
        this.mapper = mapper;
    }

    void save(OccurrenceEntity occurrence) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapper.convertDomain(occurrence))
            .build();

        putItem(request, CalendarMonitoringFunctionality.SAVE_OCCURRENCE);
    }

    List<OccurrenceEntity> getByEventId(String eventId) {
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

        return query(request, CalendarMonitoringFunctionality.GET_OCCURRENCES_BY_EVENT_ID)
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    void delete(String eventId, List<String> occurrenceIds) {
        List<WriteRequest> requests = occurrenceIds.stream()
            .map(item -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_EVENT + eventId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_OCCURRENCE + item).build()
            ))
            .map(key -> DeleteRequest.builder().key(key).build())
            .map(deleteRequest -> WriteRequest.builder().deleteRequest(deleteRequest).build())
            .toList();

        batchWrite(requests, CalendarMonitoringFunctionality.DELETE_OCCURRENCES);
    }

    void save(List<OccurrenceEntity> occurrences) {
        List<WriteRequest> requests = occurrences.stream()
            .map(mapper::convertDomain)
            .map(item -> PutRequest.builder().item(item).build())
            .map(putRequest -> WriteRequest.builder().putRequest(putRequest).build())
            .toList();

        batchWrite(requests, CalendarMonitoringFunctionality.SAVE_OCCURRENCES);
    }

    Optional<OccurrenceEntity> findById(String eventId, String occurrenceId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_EVENT + eventId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_OCCURRENCE + occurrenceId).build()
            ))
            .build();

        return mapper.convertEntity(getItem(request, CalendarMonitoringFunctionality.FIND_OCCURRENCE_BY_ID));
    }

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

        return query(request, CalendarMonitoringFunctionality.GET_OCCURRENCE_BY_BUCKET)
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    /**
     * @param occurrences List<BiWrapper<EventId, OccurrenceId>>
     */
    void delete(List<BiWrapper<String, String>> occurrences) {
        List<WriteRequest> requests = occurrences.stream()
            .map(item -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_EVENT + item.getEntity1()).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_OCCURRENCE + item.getEntity2()).build()
            ))
            .map(key -> DeleteRequest.builder().key(key).build())
            .map(deleteRequest -> WriteRequest.builder().deleteRequest(deleteRequest).build())
            .toList();

        batchWrite(requests, CalendarMonitoringFunctionality.DELETE_OCCURRENCES);
    }
}
