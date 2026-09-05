package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDynamoDbConfiguration;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarMonitoringFunctionality;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_EVENT_LABEL_MAPPING;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_LABEL_EVENT_MAPPING;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
@Slf4j
class EventLabelMappingRepository extends DynamoDbRepository {
    private final LabelEventMappingMapper labelEventMappingMapper;
    private final EventLabelMappingMapper eventLabelMappingMapper;

    EventLabelMappingRepository(
        CalendarDynamoDbConfiguration configuration,
        DynamoDbRepositoryContext context,
        LabelEventMappingMapper labelEventMappingMapper,
        EventLabelMappingMapper eventLabelMappingMapper
    ) {
        super(configuration.getCalendarTableName(), context);
        this.labelEventMappingMapper = labelEventMappingMapper;
        this.eventLabelMappingMapper = eventLabelMappingMapper;
    }

    Optional<LabelEventMappingEntity> getEventsOfLabel(String userId, String labelId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL_EVENT_MAPPING + labelId).build()
            ))
            .build();

        return getItem(request, CalendarMonitoringFunctionality.GET_EVENTS_OF_LABEL)
            .map(labelEventMappingMapper::convertEntity);
    }

    List<EventLabelMappingEntity> getLabelsOfEvents(List<BiWrapper<String, String>> ids) {
        List<Map<String, AttributeValue>> keys = ids.stream()
            .map(bw -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + bw.getEntity1()).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT_LABEL_MAPPING + bw.getEntity2()).build()
            ))
            .toList();

        return eventLabelMappingMapper.convertEntity(batchGetItem(keys, CalendarMonitoringFunctionality.GET_LABELS_OF_EVENTS));
    }

    public EventLabelMappingEntity getLabelsOfEvent(String userId, String eventId) {
        Map<String, AttributeValue> key = Map.of(
            COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
            COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT_LABEL_MAPPING + eventId).build()
        );

        return eventLabelMappingMapper.convertEntity(getItem(key, CalendarMonitoringFunctionality.GET_LABELS_OF_EVENT))
            .orElseGet(() -> EventLabelMappingEntity.builder()
                .userId(userId)
                .eventId(eventId)
                .labelIds(new HashMap<>())
                .build());
    }

    List<EventLabelMappingEntity> getLabelsOfEventsByUserId(String userId) {
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

    List<LabelEventMappingEntity> getEventsOfLabels(String userId, List<String> labelIds) {
        List<Map<String, AttributeValue>> keys = labelIds.stream()
            .map(id -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL_EVENT_MAPPING + id).build()
            ))
            .toList();
        return labelEventMappingMapper.convertEntity(batchGetItem(keys, CalendarMonitoringFunctionality.GET_EVENTS_OF_LABELS));
    }

    void saveEventsOfLabels(List<LabelEventMappingEntity> mappings) {
        mappings.forEach(mapping -> log.info("Saving events {} of label {} of user {}.", mapping.getEventIds(), mapping.getLabelId(), mapping.getUserId()));

        List<WriteRequest> requests = mappings.stream()
            .map(labelEventMappingMapper::convertDomain)
            .map(item -> PutRequest.builder().item(item).build())
            .map(putRequest -> WriteRequest.builder().putRequest(putRequest).build())
            .toList();

        batchWrite(requests, CalendarMonitoringFunctionality.SAVE_EVENTS_OF_LABELS);
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

    void deleteEventsOfLabel(String userId, String labelId) {
        log.info("Deleting Event mappings of label {} of user {}.", labelId, userId);
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL_EVENT_MAPPING + labelId).build()
            ))
            .build();

        deleteItem(request, CalendarMonitoringFunctionality.DELETE_EVENTS_OF_LABEL);
    }

    List<LabelEventMappingEntity> getEventsOfLabelsByUserId(String userId) {
        QueryRequest request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("#pk = :userId AND begins_with(#sk, :prefix)")
            .expressionAttributeNames(Map.of(
                "#pk", COLUMN_PK,
                "#sk", COLUMN_SK
            ))
            .expressionAttributeValues(Map.of(
                ":userId", AttributeValue.builder().s(PREFIX_USER + userId).build(),
                ":prefix", AttributeValue.builder().s(PREFIX_LABEL_EVENT_MAPPING).build()
            ))
            .build();

        return query(request, CalendarMonitoringFunctionality.GET_EVENTS_OF_LABELS_BY_USER_ID)
            .stream()
            .map(labelEventMappingMapper::convertEntity)
            .toList();
    }

    void saveEventsOfLabels(LabelEventMappingEntity mapping) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(labelEventMappingMapper.convertDomain(mapping))
            .build();

        putItem(request, CalendarMonitoringFunctionality.SAVE_EVENTS_OF_LABEL);
    }

     void saveLabelsOfEvents(List<EventLabelMappingEntity> mappings) {
        mappings.forEach(mapping -> log.info("Saving labels {} of event {} of user {}.", mapping.getEventId(), mapping.getLabelIds(), mapping.getUserId()));

        List<WriteRequest> requests = mappings.stream()
            .map(eventLabelMappingMapper::convertDomain)
            .map(item -> PutRequest.builder().item(item).build())
            .map(putRequest -> WriteRequest.builder().putRequest(putRequest).build())
            .toList();

        batchWrite(requests, CalendarMonitoringFunctionality.SAVE_LABELS_OF_EVENTS);
    }
}
