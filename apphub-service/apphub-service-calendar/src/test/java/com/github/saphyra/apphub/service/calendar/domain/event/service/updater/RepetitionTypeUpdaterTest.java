package com.github.saphyra.apphub.service.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RepetitionTypeUpdaterTest {
    @InjectMocks
    private RepetitionTypeUpdater underTest;

    @Mock
    private EventRequest request;

    @Mock
    private Event event;

    @Mock
    private UpdateEventContext context;

    @Test
    void getRequestField() {
        given(request.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);

        assertThat(underTest.getRequestField(request)).isEqualTo(RepetitionType.EVERY_X_DAYS);
    }

    @Test
    void getEventField() {
        given(event.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);

        assertThat(underTest.getEventField(event)).isEqualTo(RepetitionType.EVERY_X_DAYS);
    }

    @Test
    void doUpdate() {
        given(request.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);

        underTest.doUpdate(context, request, event);

        then(event).should().setRepetitionType(RepetitionType.EVERY_X_DAYS);
        then(context).should().occurrenceRecreationNeeded();
    }
}