package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDynamoDbConfiguration;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarMonitoringFunctionality;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
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
    private final EventMapper mapper;

    EventRepository(CalendarDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, EventMapper mapper) {
        super(configuration.getCalendarTableName(), context);
        this.mapper = mapper;
    }

    Optional<EventEntity> findById(String userId, String eventId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT + eventId).build()
            ))
            .build();

        return mapper.convertEntity(getItem(request, CalendarMonitoringFunctionality.FIND_EVENT_BY_ID));
    }

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

        return query(request, CalendarMonitoringFunctionality.GET_EVENTS_BY_USER_ID)
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    List<EventEntity> getByIds(List<BiWrapper<String, String>> ids) {
        List<Map<String, AttributeValue>> keys = ids.stream()
            .map(id -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + id.getEntity1()).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT + id.getEntity2()).build()
            ))
            .toList();

        return batchGetItem(keys, CalendarMonitoringFunctionality.GET_EVENTS_BY_IDS)
            .stream()
            .map(mapper::convertEntity)
            .toList();
    }

    void save(EventEntity eventEntity) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapper.convertDomain(eventEntity))
            .build();

        putItem(request, CalendarMonitoringFunctionality.SAVE_EVENT);
    }

    void delete(String userId, List<String> eventIds) {
        List<WriteRequest> requests = eventIds.stream()
            .map(eventId -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT + eventId).build()
            ))
            .map(key -> DeleteRequest.builder().key(key).build())
            .map(deleteRequest -> WriteRequest.builder().deleteRequest(deleteRequest).build())
            .toList();

        batchWrite(requests, CalendarMonitoringFunctionality.DELETE_EVENTS);
    }
}
