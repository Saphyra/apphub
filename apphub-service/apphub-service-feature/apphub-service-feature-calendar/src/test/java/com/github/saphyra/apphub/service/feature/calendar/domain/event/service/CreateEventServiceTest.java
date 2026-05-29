package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.service.EventLabelMappingService;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.CreateOccurrenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CreateEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private EventRequestValidator eventRequestValidator;

    @Mock
    private DeprecatedEventFactory eventFactory;

    @Mock
    private EventLabelMappingService eventLabelMappingService;

    @Mock
    private CreateOccurrenceService createOccurrenceService;

    @Mock
    private DeprecatedEventDao eventDao;

    @InjectMocks
    private CreateEventService underTest;

    @Mock
    private EventRequest request;

    @Mock
    private DeprecatedEvent event;

    @Test
    void create() {
        given(eventFactory.create(USER_ID, request)).willReturn(event);
        given(event.getEventId()).willReturn(EVENT_ID);

        assertThat(underTest.create(USER_ID, request)).isEqualTo(EVENT_ID);

        then(eventRequestValidator).should().validate(request);
        then(eventLabelMappingService).should().addLabels(USER_ID, EVENT_ID, request.getLabels());
        then(createOccurrenceService).should().createOccurrences(USER_ID, EVENT_ID, request);
        then(eventDao).should().save(event);
    }
}