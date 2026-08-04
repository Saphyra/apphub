package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContextFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.ObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.Operation;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();

    @Mock
    private EventRequestValidator eventRequestValidator;

    @Mock
    private UpdateEventContextFactory updateEventContextFactory;

    @Mock
    private EventFieldUpdater eventFieldUpdater;

    @Mock
    private ObjectQueryService objectQueryService;

    private EditEventService underTest;

    @Mock
    private EventRequest request;

    @Mock
    private Event event;

    @Mock
    private UpdateEventContext context;

    @BeforeEach
    void setUp(){
        underTest = EditEventService.builder()
            .eventRequestValidator(eventRequestValidator)
            .updateEventContextFactory(updateEventContextFactory)
            .eventFieldUpdaters(List.of(eventFieldUpdater))
            .objectQueryService(objectQueryService)
            .build();
    }

    @Test
    void eventNotFound() {
        given(objectQueryService.findEvent(USER_ID, EVENT_ID, Operation.EDIT)).willReturn(Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.edit(USER_ID, EVENT_ID, request));

        then(eventRequestValidator).should().validateEdit(request);
    }

    @Test
    void edit() {
        given(objectQueryService.findEvent(USER_ID, EVENT_ID, Operation.EDIT)).willReturn(Optional.of(event));
        given(updateEventContextFactory.create(event)).willReturn(context);
        given(request.getLabels()).willReturn(Map.of(LABEL_ID, USER_ID));

        underTest.edit(USER_ID, EVENT_ID, request);

        then(eventRequestValidator).should().validateEdit(request);
        then(event).should().setExpirationNotified(false);
        then(eventFieldUpdater).should().update(context, request, event);
        then(context).should().processChanges(Map.of(LABEL_ID, USER_ID));
    }
}