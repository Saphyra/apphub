package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDynamoDbConfiguration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_EVENT_IDS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_LABEL_IDS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_EVENT_LABEL_MAPPING;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_LABEL_EVENT_MAPPING;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
class EventLabelMappingRepository extends DynamoDbRepository {
    private final ObjectMapper objectMapper;

    EventLabelMappingRepository(CalendarDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, ObjectMapper objectMapper) {
        super(configuration.getTableName(), context);
        this.objectMapper = objectMapper;
    }

    List<String> getEventsOfLabel(String userId, String labelId) {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL_EVENT_MAPPING + labelId).build()
            ))
            .build();

        return Optional.of(client.getItem(request))
            .filter(GetItemResponse::hasItem)
            .map(GetItemResponse::item)
            .map(item -> item.get(COLUMN_EVENT_IDS).s())
            .map(s -> objectMapper.readValue(s, new TypeReference<List<String>>() {
            }))
            .orElse(List.of());
    }

    List<BiWrapper<String, List<String>>> getLabelsOfEvents(String userId, List<String> eventIds) {
        List<Map<String, AttributeValue>> keys = eventIds.stream()
            .map(id -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT_LABEL_MAPPING + id).build()
            ))
            .toList();

        return batchGetItem(keys)
            .stream()
            .map(item -> new BiWrapper<>(
                item.get(COLUMN_SK).s().substring(PREFIX_EVENT_LABEL_MAPPING.length()),
                objectMapper.readValue(item.get(COLUMN_LABEL_IDS).s(), new TypeReference<List<String>>() {
                })
            ))
            .toList();
    }

    List<BiWrapper<String, List<String>>> getLabelsOfEventsByUserId(String userId) {
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

        return query(request)
            .stream()
            .map(item -> new BiWrapper<>(
                item.get(COLUMN_SK).s().substring(PREFIX_EVENT_LABEL_MAPPING.length()),
                objectMapper.readValue(item.get(COLUMN_LABEL_IDS).s(), new TypeReference<List<String>>() {
                })
            ))
            .toList();
    }

    void saveLabelsOfEvent(String userId, String eventId, List<String> labelIds) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT_LABEL_MAPPING + eventId).build(),
                COLUMN_LABEL_IDS, AttributeValue.builder().s(objectMapper.writeValueAsString(labelIds)).build()
            ))
            .build();

        client.putItem(request);
    }

    List<BiWrapper<String, List<String>>> getEventsOfLabels(String userId, List<String> labelIds) {
        List<Map<String, AttributeValue>> keys = labelIds.stream()
            .map(id -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL_EVENT_MAPPING + id).build()
            ))
            .toList();
        return batchGetItem(keys)
            .stream()
            .map(item -> new BiWrapper<>(
                item.get(COLUMN_SK).s().substring(PREFIX_LABEL_EVENT_MAPPING.length()),
                objectMapper.readValue(item.get(COLUMN_EVENT_IDS).s(), new TypeReference<List<String>>() {
                })
            ))
            .toList();
    }

    /**
     * @param mappings <LabelId, List<EventId>>
     */
    void saveEventsOfLabels(String userId, List<BiWrapper<String, List<String>>> mappings) {
        List<WriteRequest> requests = mappings.stream()
            .map(mapping -> Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL_EVENT_MAPPING + mapping.getEntity1()).build(),
                COLUMN_EVENT_IDS, AttributeValue.builder().s(objectMapper.writeValueAsString(mapping.getEntity2())).build()
            ))
            .map(item -> PutRequest.builder().item(item).build())
            .map(putRequest -> WriteRequest.builder().putRequest(putRequest).build())
            .toList();

        batchWrite(requests);
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

        batchWrite(requests);
    }

    void deleteEventsOfLabel(String userId, String labelId) {
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL_EVENT_MAPPING + labelId).build()
            ))
            .build();

        client.deleteItem(request);
    }

    List<BiWrapper<String, List<String>>> getEventsOfLabelsByUserId(String userId) {
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

        return query(request)
            .stream()
            .map(item -> new BiWrapper<>(
                item.get(COLUMN_SK).s().substring(PREFIX_LABEL_EVENT_MAPPING.length()),
                objectMapper.readValue(item.get(COLUMN_EVENT_IDS).s(), new TypeReference<List<String>>() {
                })
            ))
            .toList();
    }

    void saveEventsOfLabels(String userId, String labelId, List<String> eventIds) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(Map.of(
                COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + userId).build(),
                COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL_EVENT_MAPPING + labelId).build(),
                COLUMN_EVENT_IDS, AttributeValue.builder().s(objectMapper.writeValueAsString(eventIds)).build()
            ))
            .build();

        client.putItem(request);
    }
}
