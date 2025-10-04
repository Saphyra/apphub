package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContextFactory;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
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
        given(eventDao.findByIdValidated(EVENT_ID)).willReturn(event);
        given(updateEventContextFactory.create(event)).willReturn(context);

        underTest.edit(EVENT_ID, request);

        then(eventRequestValidator).should().validate(request);
        then(eventFieldUpdater).should().update(context, request, event);
        then(context).should().processChanges();
    }
}