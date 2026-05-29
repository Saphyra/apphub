package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TimeUpdaterTest {
    private static final LocalTime TIME = LocalTime.of(12, 34);

    @InjectMocks
    private TimeUpdater underTest;

    @Mock
    private EventRequest request;

    @Mock
    private DeprecatedEvent event;

    @Mock
    private UpdateEventContext context;

    @Test
    void getRequestField() {
        given(request.getTime()).willReturn(TIME);

        assertThat(underTest.getRequestField(request)).isEqualTo(TIME);
    }

    @Test
    void getEventField() {
        given(event.getTime()).willReturn(TIME);

        assertThat(underTest.getEventField(event)).isEqualTo(TIME);
    }

    @Test
    void doUpdate() {
        given(request.getTime()).willReturn(TIME);

        underTest.doUpdate(context, request, event);

        then(event).should().setTime(TIME);
    }
}