package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@Deprecated(forRemoval = true)
class DeprecatedEventLabelMappingFactoryTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();

    @InjectMocks
    private DeprecatedEventLabelMappingFactory underTest;

    @Test
    void create() {
        assertThat(underTest.create(USER_ID, EVENT_ID, LABEL_ID))
            .returns(USER_ID, DeprecatedEventLabelMapping::getUserId)
            .returns(EVENT_ID, DeprecatedEventLabelMapping::getEventId)
            .returns(LABEL_ID, DeprecatedEventLabelMapping::getLabelId);
    }
}