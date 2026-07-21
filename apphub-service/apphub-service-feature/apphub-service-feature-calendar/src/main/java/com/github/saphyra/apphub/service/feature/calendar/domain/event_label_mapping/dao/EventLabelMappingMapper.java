package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_LABEL_IDS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_EVENT_LABEL_MAPPING;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;

@Component
@RequiredArgsConstructor
//TODO unit test
class EventLabelMappingMapper extends ConverterBase<Map<String, AttributeValue>, EventLabelMappingEntity> {
    private final ObjectMapper objectMapper;

    @Override
    protected Map<String, AttributeValue> processDomainConversion(EventLabelMappingEntity domain) {
        return Map.of(
            COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + domain.getUserId()).build(),
            COLUMN_SK, AttributeValue.builder().s(PREFIX_EVENT_LABEL_MAPPING + domain.getEventId()).build(),
            COLUMN_LABEL_IDS, AttributeValue.builder().s(objectMapper.writeValueAsString(domain.getLabelIds())).build()
        );
    }

    @Override
    protected EventLabelMappingEntity processEntityConversion(Map<String, AttributeValue> entity) {
        return EventLabelMappingEntity.builder()
            .userId(entity.get(COLUMN_PK).s().substring(PREFIX_USER.length()))
            .eventId(entity.get(COLUMN_SK).s().substring(PREFIX_EVENT_LABEL_MAPPING.length()))
            .labelIds(objectMapper.readValue(entity.get(COLUMN_LABEL_IDS).s(), new TypeReference<StringStringMap>() {
            }))
            .build();
    }
}
