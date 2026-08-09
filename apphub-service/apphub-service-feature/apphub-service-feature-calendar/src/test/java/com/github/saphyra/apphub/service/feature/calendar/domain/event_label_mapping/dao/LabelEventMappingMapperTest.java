package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_EVENT_IDS;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_LABEL_EVENT_MAPPING;
import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.PREFIX_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LabelEventMappingMapperTest {
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String EVENT_ID = "event-id";
    private static final String LABEL_ID = "label-id";
    private static final String SERIALIZED_EVENT_IDS = "serialized-event-ids";

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LabelEventMappingMapper underTest;

    @Test
    void convertDomain() {
        LabelEventMappingEntity domain = LabelEventMappingEntity.builder()
            .userId(USER_ID_1)
            .labelId(LABEL_ID)
            .eventIds(Map.of(EVENT_ID, USER_ID_2))
            .build();

        given(objectMapper.writeValueAsString(domain.getEventIds())).willReturn(SERIALIZED_EVENT_IDS);

        assertThat(underTest.convertDomain(domain))
            .returns(USER_ID_1, map -> map.get(COLUMN_PK).s().substring(PREFIX_USER.length()))
            .returns(LABEL_ID, map -> map.get(COLUMN_SK).s().substring(PREFIX_LABEL_EVENT_MAPPING.length()))
            .returns(SERIALIZED_EVENT_IDS, map -> map.get(COLUMN_EVENT_IDS).s());
    }

    @Test
    void convertEntity() {
        StringStringMap eventIds = new StringStringMap(Map.of(EVENT_ID, USER_ID_2));

        Map<String, AttributeValue> entity = Map.of(
            COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + USER_ID_1).build(),
            COLUMN_SK, AttributeValue.builder().s(PREFIX_LABEL_EVENT_MAPPING + LABEL_ID).build(),
            COLUMN_EVENT_IDS, AttributeValue.builder().s(SERIALIZED_EVENT_IDS).build()
        );

        given(objectMapper.readValue(eq(SERIALIZED_EVENT_IDS), any(TypeReference.class))).willReturn(eventIds);

        assertThat(underTest.convertEntity(entity))
            .returns(USER_ID_1, LabelEventMappingEntity::getUserId)
            .returns(LABEL_ID, LabelEventMappingEntity::getLabelId)
            .returns(eventIds, LabelEventMappingEntity::getEventIds);
    }
}