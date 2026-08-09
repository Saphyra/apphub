package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LabelEventMappingConverterTest {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final String USER_ID_1_STRING = "user-id-1";
    private static final String EVENT_ID_STRING = "event-id";
    private static final String LABEL_ID_STRING = "label-id";
    private static final String USER_ID_2_STRING = "user-id-2";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private LabelEventMappingConverter underTest;

    @Test
    void convertDomain() {
        LabelEventMapping domain = LabelEventMapping.builder()
            .userId(USER_ID_1)
            .labelId(LABEL_ID)
            .eventIds(Map.of(EVENT_ID, USER_ID_2))
            .build();

        given(uuidConverter.convertDomain(USER_ID_1)).willReturn(USER_ID_1_STRING);
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID_2)).willReturn(USER_ID_2_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(USER_ID_1_STRING, LabelEventMappingEntity::getUserId)
            .returns(LABEL_ID_STRING, LabelEventMappingEntity::getLabelId)
            .returns(Map.of(EVENT_ID_STRING, USER_ID_2_STRING), LabelEventMappingEntity::getEventIds);
    }

    @Test
    void convertEntity() {
        LabelEventMappingEntity entity = LabelEventMappingEntity.builder()
            .userId(USER_ID_1_STRING)
            .labelId(LABEL_ID_STRING)
            .eventIds(Map.of(EVENT_ID_STRING, USER_ID_2_STRING))
            .build();

        given(uuidConverter.convertEntity(USER_ID_1_STRING)).willReturn(USER_ID_1);
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(LABEL_ID_STRING)).willReturn(LABEL_ID);
        given(uuidConverter.convertEntity(USER_ID_2_STRING)).willReturn(USER_ID_2);

        assertThat(underTest.convertEntity(entity))
            .returns(USER_ID_1, LabelEventMapping::getUserId)
            .returns(LABEL_ID, LabelEventMapping::getLabelId)
            .returns(Map.of(EVENT_ID, USER_ID_2), LabelEventMapping::getEventIds);
    }
}