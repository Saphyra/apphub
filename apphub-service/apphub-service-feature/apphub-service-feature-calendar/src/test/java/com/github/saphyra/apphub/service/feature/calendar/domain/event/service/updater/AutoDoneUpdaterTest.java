package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AutoDoneUpdaterTest {
    @InjectMocks
    private AutoDoneUpdater underTest;

    @Mock
    private EventRequest request;

    @Mock
    private Event event;

    @Mock
    private UpdateEventContext updateEventContext;

    @Test
    void getRequestField(){
        given(request.getAutoDone()).willReturn(true);

        assertThat(underTest.getRequestField(request)).isEqualTo(true);
    }

    @Test
    void getEventField(){
        given(event.isAutoDone()).willReturn(true);

        assertThat(underTest.getEventField(event)).isEqualTo(true);
    }

    @Test
    void doUpdate(){
        given(request.getAutoDone()).willReturn(true);

        underTest.doUpdate(updateEventContext, request, event);

        then(event).should().setAutoDone(true);
    }
}