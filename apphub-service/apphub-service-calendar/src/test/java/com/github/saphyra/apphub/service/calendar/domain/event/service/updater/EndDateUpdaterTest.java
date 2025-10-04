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
class EndDateUpdaterTest {
    private static final LocalDate END_DATE = LocalDate.now();

    @InjectMocks
    private EndDateUpdater underTest;

    @Mock
    private EventRequest request;

    @Mock
    private Event event;

    @Mock
    private UpdateEventContext context;

    @Test
    void getRequestField() {
        given(request.getEndDate()).willReturn(END_DATE);

        assertThat(underTest.getRequestField(request)).isEqualTo(END_DATE);
    }

    @Test
    void getEventField() {
        given(event.getEndDate()).willReturn(END_DATE);

        assertThat(underTest.getEventField(event)).isEqualTo(END_DATE);
    }

    @Test
    void doUpdate() {
        given(request.getEndDate()).willReturn(END_DATE);

        underTest.doUpdate(context, request, event);

        then(event).should().setEndDate(END_DATE);
        then(context).should().occurrenceRecreationNeeded();
    }
}