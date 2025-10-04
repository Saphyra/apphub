package com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventLabelMappingConverterTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String EVENT_ID_STRING = "event-id";
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final String LABEL_ID_STRING = "label-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private EventLabelMappingConverter underTest;

    @Test
    void convertDomain() {
        EventLabelMapping domain = EventLabelMapping.builder()
            .userId(USER_ID)
            .eventId(EVENT_ID)
            .labelId(LABEL_ID)
            .build();

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(USER_ID_STRING, EventLabelMappingEntity::getUserId)
            .returns(EVENT_ID_STRING, EventLabelMappingEntity::getEventId)
            .returns(LABEL_ID_STRING, EventLabelMappingEntity::getLabelId);
    }

    @Test
    void convertEntity() {
        EventLabelMappingEntity entity = EventLabelMappingEntity.builder()
            .userId(USER_ID_STRING)
            .eventId(EVENT_ID_STRING)
            .labelId(LABEL_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(LABEL_ID_STRING)).willReturn(LABEL_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(USER_ID, EventLabelMapping::getUserId)
            .returns(EVENT_ID, EventLabelMapping::getEventId)
            .returns(LABEL_ID, EventLabelMapping::getLabelId);
    }
}