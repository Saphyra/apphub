package com.github.saphyra.apphub.service.calendar.domain.event.service.updater;

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
class ArchivedUpdaterTest {
    @InjectMocks
    private ArchivedUpdater underTest;

    @Mock
    private EventRequest request;

    @Mock
    private Event event;

    @Mock
    private UpdateEventContext context;

    @Test
    void getRequestField() {
        given(request.getArchived()).willReturn(true);

        assertThat(underTest.getRequestField(request)).isEqualTo(true);
    }

    @Test
    void getEventField() {
        given(event.isArchived()).willReturn(true);

        assertThat(underTest.getEventField(event)).isEqualTo(true);
    }

    @Test
    void doUpdate() {
        given(request.getArchived()).willReturn(true);

        underTest.doUpdate(context, request, event);

        then(event).should().setArchived(true);
    }
}

