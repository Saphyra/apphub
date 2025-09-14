package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventFactory;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.service.EventLabelMappingService;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.CreateOccurrenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CreateEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private EventRequestValidator eventRequestValidator;

    @Mock
    private EventFactory eventFactory;

    @Mock
    private EventLabelMappingService eventLabelMappingService;

    @Mock
    private CreateOccurrenceService createOccurrenceService;

    @Mock
    private EventDao eventDao;

    @InjectMocks
    private CreateEventService underTest;

    @Mock
    private EventRequest request;

    @Mock
    private Event event;

    @Test
    void create() {
        given(eventFactory.create(USER_ID, request)).willReturn(event);
        given(event.getEventId()).willReturn(EVENT_ID);

        underTest.create(USER_ID, request);

        then(eventRequestValidator).should().validate(request);
        then(eventLabelMappingService).should().addLabels(USER_ID, EVENT_ID, request.getLabels());
        then(createOccurrenceService).should().createOccurrences(USER_ID, EVENT_ID, request);
        then(eventDao).should().save(event);
    }
}