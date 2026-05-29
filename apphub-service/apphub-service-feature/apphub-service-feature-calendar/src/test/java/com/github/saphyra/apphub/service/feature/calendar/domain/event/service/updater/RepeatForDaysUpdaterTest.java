package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RepeatForDaysUpdaterTest {
    private static final Integer REPEAT_FOR_DAYS = 123;

    @InjectMocks
    private RepeatForDaysUpdater underTest;

    @Mock
    private EventRequest request;

    @Mock
    private DeprecatedEvent event;

    @Mock
    private UpdateEventContext context;

    @Test
    void getRequestField() {
        given(request.getRepeatForDays()).willReturn(REPEAT_FOR_DAYS);

        assertThat(underTest.getRequestField(request)).isEqualTo(REPEAT_FOR_DAYS);
    }

    @Test
    void getEventField() {
        given(event.getRepeatForDays()).willReturn(REPEAT_FOR_DAYS);

        assertThat(underTest.getEventField(event)).isEqualTo(REPEAT_FOR_DAYS);
    }

    @Test
    void doUpdate() {
        given(request.getRepeatForDays()).willReturn(REPEAT_FOR_DAYS);

        underTest.doUpdate(context, request, event);

        then(event).should().setRepeatForDays(REPEAT_FOR_DAYS);
        then(context).should().occurrenceRecreationNeeded();
    }
}