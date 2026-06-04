package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContextFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditEventServiceTest {
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();

    @Mock
    private EventRequestValidator eventRequestValidator;

    @Mock
    private EventDao eventDao;

    @Mock
    private UpdateEventContextFactory updateEventContextFactory;

    @Mock
    private EventFieldUpdater eventFieldUpdater;

    @InjectMocks
    private EditEventService underTest;

    @Mock
    private EventRequest request;

    @Mock
    private Event event;

    @Mock
    private UpdateEventContext context;

    @BeforeEach
    void setUp() {
        underTest = EditEventService.builder()
            .eventRequestValidator(eventRequestValidator)
            .eventDao(eventDao)
            .updateEventContextFactory(updateEventContextFactory)
            .eventFieldUpdaters(List.of(eventFieldUpdater))
            .build();
    }

    @Test
    void edit() {
        given(eventDao.findByIdValidated(USER_ID, EVENT_ID)).willReturn(event);
        given(updateEventContextFactory.create(event)).willReturn(context);
        given(request.getLabels()).willReturn(List.of(LABEL_ID));

        underTest.edit(USER_ID, EVENT_ID, request);

        then(event).should().setExpirationNotified(false);
        then(eventRequestValidator).should().validateEdit(USER_ID, request);
        then(eventFieldUpdater).should().update(context, request, event);
        then(context).should().processChanges(List.of(LABEL_ID));
    }
}