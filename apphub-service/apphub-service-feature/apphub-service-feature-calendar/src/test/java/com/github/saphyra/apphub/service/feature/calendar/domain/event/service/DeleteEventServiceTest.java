package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query.EventObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.common.Operation;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeleteEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private CommonCalendarDao commonCalendarDao;

    @Mock
    private EventObjectQueryService eventObjectQueryService;

    @InjectMocks
    private DeleteEventService underTest;

    @Mock
    private Event event;

    @Test
    void delete() {
        given(eventObjectQueryService.findEvent(USER_ID, EVENT_ID, Operation.DELETE)).willReturn(Optional.of(event));
        given(event.getUserId()).willReturn(USER_ID);
        given(event.getEventId()).willReturn(EVENT_ID);

        underTest.delete(USER_ID, EVENT_ID);

        then(commonCalendarDao).should().deleteEvents(USER_ID, List.of(EVENT_ID));
    }
}