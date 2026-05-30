package com.github.saphyra.apphub.service.feature.calendar.common.context;

import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
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
    private EventDao eventDao;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private RecreateOccurrenceService recreateOccurrenceService;

    @InjectMocks
    private UpdateEventContextFactory underTest;

    @Mock
    private Event event;

    @Test
    void create() {
        assertThat(underTest.create(event)).isNotNull();
    }
}