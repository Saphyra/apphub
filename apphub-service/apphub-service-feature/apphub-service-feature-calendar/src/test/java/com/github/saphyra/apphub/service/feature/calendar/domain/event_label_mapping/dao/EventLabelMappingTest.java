package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EventLabelMappingTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID LABEL_ID_1 = UUID.randomUUID();
    private static final UUID LABEL_ID_2 = UUID.randomUUID();

    @Test
    void removeLabelId() {
        EventLabelMapping underTest = new EventLabelMapping(USER_ID, EVENT_ID, Map.of(LABEL_ID_1, USER_ID, LABEL_ID_2, USER_ID));

        assertThat(underTest.removeLabelId(LABEL_ID_1))
            .extracting(EventLabelMapping::getLabelIds)
            .isEqualTo(Map.of(LABEL_ID_2, USER_ID));
    }
}