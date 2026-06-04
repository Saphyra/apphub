package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.CreateOccurrenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CreateEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();

    @Mock
    private EventRequestValidator eventRequestValidator;

    @Mock
    private EventFactory eventFactory;

    @Mock
    private CreateOccurrenceService createOccurrenceService;

    @Mock
    private CommonCalendarDao commonCalendarDao;

    @InjectMocks
    private CreateEventService underTest;

    @Mock
    private EventRequest request;

    @Mock
    private Event event;

    @Mock
    private Occurrence occurrence;

    @Test
    void create() {
        given(eventFactory.create(USER_ID, request)).willReturn(event);
        given(event.getEventId()).willReturn(EVENT_ID);
        given(createOccurrenceService.createOccurrences(USER_ID, EVENT_ID, request)).willReturn(List.of(occurrence));
        given(request.getLabels()).willReturn(List.of(LABEL_ID));

        assertThat(underTest.create(USER_ID, request)).isEqualTo(EVENT_ID);

        then(eventRequestValidator).should().validate(USER_ID, request);
        then(createOccurrenceService).should().createOccurrences(USER_ID, EVENT_ID, request);
        then(commonCalendarDao).should().saveNewEvent(event, List.of(occurrence), List.of(LABEL_ID));
    }
}