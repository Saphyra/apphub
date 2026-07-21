package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping;

import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepository;
import com.github.saphyra.apphub.lib.dynamodb.DynamoDbRepositoryContext;
import com.github.saphyra.apphub.lib.dynamodb.MonitoringFunctionality;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDynamoDbConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_EVENT_IDS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_LABEL_IDS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.MIGRATION;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_EVENT_LABEL_MAPPING;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_LABEL_EVENT_MAPPING;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
@Slf4j
@Profile("!test")
//TODO unit test
class EventLabelMappingMigrator extends DynamoDbRepository {
    private static final String EVENT_LABEL_MAPPING_ADD_UID_TO_LABEL_IDS = "event_label_mapping-add_uid_to_label_ids";
    private static final String LABEL_EVENT_MAPPING_ADD_UID_TO_LABEL_IDS = "label_event_mapping-add_uid_to_label_ids";

    private final ObjectMapper objectMapper;

    EventLabelMappingMigrator(CalendarDynamoDbConfiguration configuration, DynamoDbRepositoryContext context, ObjectMapper objectMapper) {
        super(configuration.getCalendarTableName(), context);
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void migrateEventLabelMapping() {
        Map<String, AttributeValue> lock = Map.of(
            COLUMN_PK, AttributeValue.builder().s(MIGRATION).build(),
            COLUMN_SK, AttributeValue.builder().s(EVENT_LABEL_MAPPING_ADD_UID_TO_LABEL_IDS).build()
        );
        if (getItem(lock, MonitoringFunctionality.UNMONITORED).isPresent()) {
            log.info("EventLabelMapping migration already performed.");
            return;
        }

        log.info("Migrating EventLabelMappings...");

        ScanRequest request = ScanRequest.builder()
            .filterExpression("begins_with(#sk, :sk)")
            .expressionAttributeNames(Map.of("#sk", COLUMN_SK))
            .expressionAttributeValues(Map.of(":sk", AttributeValue.builder().s(PREFIX_EVENT_LABEL_MAPPING).build()))
            .build();

        List<Map<String, AttributeValue>> result = scan(request, MonitoringFunctionality.UNMONITORED);

        List<WriteRequest> writeRequests = result.stream()
            .map(item -> {
                Map<String, AttributeValue> copy = new HashMap<>(item);

                List<String> labelIds = objectMapper.readValue(item.get(COLUMN_LABEL_IDS).s(), List.class);
                String userId = item.get(COLUMN_PK).s().substring(PREFIX_USER.length());

                Map<String, String> newLabelIds = labelIds.stream()
                    .collect(Collectors.toMap(labelId -> labelId, _ -> userId));
                copy.put(COLUMN_LABEL_IDS, AttributeValue.builder().s(objectMapper.writeValueAsString(newLabelIds)).build());

                return copy;
            })
            .map(item -> WriteRequest.builder()
                .putRequest(builder -> builder.item(item))
                .build())
            .toList();
        batchWrite(writeRequests, MonitoringFunctionality.UNMONITORED);

        putItem(lock, MonitoringFunctionality.UNMONITORED);

        log.info("{} EventLabelMappings migrated.", result.size());
    }

    @PostConstruct
    void migrateLabelEventMapping() {
        Map<String, AttributeValue> lock = Map.of(
            COLUMN_PK, AttributeValue.builder().s(MIGRATION).build(),
            COLUMN_SK, AttributeValue.builder().s(LABEL_EVENT_MAPPING_ADD_UID_TO_LABEL_IDS).build()
        );
        if (getItem(lock, MonitoringFunctionality.UNMONITORED).isPresent()) {
            log.info("LabelEventMapping migration already performed.");
            return;
        }

        log.info("Migrating LabelEventMappings...");

        ScanRequest request = ScanRequest.builder()
            .filterExpression("begins_with(#sk, :sk)")
            .expressionAttributeNames(Map.of("#sk", COLUMN_SK))
            .expressionAttributeValues(Map.of(":sk", AttributeValue.builder().s(PREFIX_LABEL_EVENT_MAPPING).build()))
            .build();

        List<Map<String, AttributeValue>> result = scan(request, MonitoringFunctionality.UNMONITORED);

        List<WriteRequest> writeRequests = result.stream()
            .map(item -> {
                Map<String, AttributeValue> copy = new HashMap<>(item);

                List<String> eventIds = objectMapper.readValue(item.get(COLUMN_EVENT_IDS).s(), List.class);
                String userId = item.get(COLUMN_PK).s().substring(PREFIX_USER.length());

                Map<String, String> newEventIds = eventIds.stream()
                    .collect(Collectors.toMap(labelId -> labelId, _ -> userId));
                copy.put(COLUMN_EVENT_IDS, AttributeValue.builder().s(objectMapper.writeValueAsString(newEventIds)).build());

                return copy;
            })
            .map(item -> WriteRequest.builder()
                .putRequest(builder -> builder.item(item))
                .build())
            .toList();
        batchWrite(writeRequests, MonitoringFunctionality.UNMONITORED);

        putItem(lock, MonitoringFunctionality.UNMONITORED);

        log.info("{} LabelEventMappings migrated.", result.size());
    }
}
