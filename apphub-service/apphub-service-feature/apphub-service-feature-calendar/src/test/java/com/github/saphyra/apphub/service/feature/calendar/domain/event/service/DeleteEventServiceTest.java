package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeleteEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private DeprecatedEventDao eventDao;

    @Mock
    private DeprecatedEventLabelMappingDao eventLabelMappingDao;

    @Mock
    private DeprecatedOccurrenceDao occurrenceDao;

    @InjectMocks
    private DeleteEventService underTest;

    @Test
    void delete() {
        underTest.delete(USER_ID, EVENT_ID);

        then(eventDao).should().deleteByUserIdAndEventId(USER_ID, EVENT_ID);
        then(eventLabelMappingDao).should().deleteByUserIdAndEventId(USER_ID, EVENT_ID);
        then(occurrenceDao).should().deleteByUserIdAndEventId(USER_ID, EVENT_ID);
    }
}