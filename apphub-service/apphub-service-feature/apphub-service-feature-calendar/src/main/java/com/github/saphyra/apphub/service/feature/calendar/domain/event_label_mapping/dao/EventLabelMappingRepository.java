package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDynamoDbConfiguration;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarMonitoringFunctionality;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_EVENT_LABEL_MAPPING;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
@Slf4j
class EventLabelMappingRepository extends DynamoDbRepository {
    private final EventLabelMappingMapper eventLabelMappingMapper;

    EventLabelMappingRepository(
        CalendarDynamoDbConfiguration configuration,
        DynamoDbRepositoryContext context,
        EventLabelMappingMapper eventLabelMappingMapper
    ) {
        super(configuration.getCalendarTableName(), context);
        this.eventLabelMappingMapper = eventLabelMappingMapper;
    }

    List<EventLabelMappingEntity> getByUserId(String userId) {
        log.info("Querying mapped labels of events of user {}.", userId);

        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :userId and begins_with(#sk, :prefix)")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":userId", AttributeValue.builder().s(PREFIX_USER + userId).build(),
                ":prefix", AttributeValue.builder().s(PREFIX_EVENT_LABEL_MAPPING).build()
            ))
            .build();

        return query(request, CalendarMonitoringFunctionality.GET_LABELS_OF_EVENTS_BY_USER_ID)
            .stream()
            .map(eventLabelMappingMapper::convertEntity)
            .peek(mapping -> log.info("Labels found for event {}: {}", mapping.getEventId(), mapping.getLabelIds()))
            .toList();
    }

    void saveLabelsOfEvent(EventLabelMappingEntity mapping) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(eventLabelMappingMapper.convertDomain(mapping))
            .build();

        putItem(request, CalendarMonitoringFunctionality.SAVE_LABELS_OF_EVENT);
    }

    void deleteLabelsOfEvents(String userId, List<String> eventIds) {
        List<WriteRequest> requests = eventIds.stream()
            .map(eventId -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT_LABEL_MAPPING + eventId).build()
            ))
            .map(key -> DeleteRequest.builder().key(key).build())
            .map(deleteRequest -> WriteRequest.builder().deleteRequest(deleteRequest).build())
            .toList();

        batchWrite(requests, CalendarMonitoringFunctionality.DELETE_LABELS_OF_EVENTS);
    }
}
