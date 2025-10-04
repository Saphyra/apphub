package com.github.saphyra.apphub.service.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StartDateUpdaterTest {
    private static final LocalDate START_DATE = LocalDate.now();

    @InjectMocks
    private StartDateUpdater underTest;

    @Mock
    private EventRequest request;

    @Mock
    private Event event;

    @Mock
    private UpdateEventContext context;

    @Test
    void getRequestField() {
        given(request.getStartDate()).willReturn(START_DATE);

        assertThat(underTest.getRequestField(request)).isEqualTo(START_DATE);
    }

    @Test
    void getEventField() {
        given(event.getStartDate()).willReturn(START_DATE);

        assertThat(underTest.getEventField(event)).isEqualTo(START_DATE);
    }

    @Test
    void doUpdate() {
        given(request.getStartDate()).willReturn(START_DATE);

        underTest.doUpdate(context, request, event);

        then(event).should().setStartDate(START_DATE);
        then(context).should().occurrenceRecreationNeeded();
    }
}