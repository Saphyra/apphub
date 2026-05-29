package com.github.saphyra.apphub.service.feature.calendar.common.context;

import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.RecreateOccurrenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UpdateEventContextFactoryTest {
    @Mock
    private DeprecatedEventDao eventDao;

    @Mock
    private DeprecatedOccurrenceDao occurrenceDao;

    @Mock
    private RecreateOccurrenceService recreateOccurrenceService;

    @InjectMocks
    private UpdateEventContextFactory underTest;

    @Mock
    private DeprecatedEvent event;

    @Test
    void create() {
        assertThat(underTest.create(event)).isNotNull();
    }
}