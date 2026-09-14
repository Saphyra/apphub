package com.github.saphyra.apphub.service.feature.calendar.domain.label_event_mapping.dao;

import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDynamoDbConfiguration;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarMonitoringFunctionality;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.util.List;
import java.util.Map;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_LABEL_EVENT_MAPPING;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
@Slf4j
class LabelEventMappingRepository extends DynamoDbRepository {
    private final LabelEventMappingMapper labelEventMappingMapper;

    LabelEventMappingRepository(
        CalendarDynamoDbConfiguration configuration,
        DynamoDbRepositoryContext context,
        LabelEventMappingMapper labelEventMappingMapper
    ) {
        super(configuration.getCalendarTableName(), context);
        this.labelEventMappingMapper = labelEventMappingMapper;
    }

    void save(List<LabelEventMappingEntity> mappings) {
        mappings.forEach(mapping -> log.info("Saving events {} of label {} of user {}.", mapping.getEventIds(), mapping.getLabelId(), mapping.getUserId()));

        List<WriteRequest> requests = mappings.stream()
            .map(labelEventMappingMapper::convertDomain)
            .map(item -> PutRequest.builder().item(item).build())
            .map(putRequest -> WriteRequest.builder().putRequest(putRequest).build())
            .toList();

        batchWrite(requests, CalendarMonitoringFunctionality.SAVE_EVENTS_OF_LABELS);
    }

    List<LabelEventMappingEntity> getByUserId(String userId) {
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

    void save(LabelEventMappingEntity mapping) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(labelEventMappingMapper.convertDomain(mapping))
            .build();

        putItem(request, CalendarMonitoringFunctionality.SAVE_EVENTS_OF_LABEL);
    }

    void delete(String userId, String labelId) {
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
}
