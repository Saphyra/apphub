package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
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
    private EventDao eventDao;

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private OccurrenceDao occurrenceDao;

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