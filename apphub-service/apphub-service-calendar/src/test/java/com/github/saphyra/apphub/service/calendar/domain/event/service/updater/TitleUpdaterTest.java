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
class TitleUpdaterTest {
    private static final String TITLE = "title";

    @InjectMocks
    private TitleUpdater underTest;

    @Mock
    private EventRequest request;

    @Mock
    private Event event;

    @Mock
    private UpdateEventContext context;

    @Test
    void getRequestField() {
        given(request.getTitle()).willReturn(TITLE);

        assertThat(underTest.getRequestField(request)).isEqualTo(TITLE);
    }

    @Test
    void getEventField() {
        given(event.getTitle()).willReturn(TITLE);

        assertThat(underTest.getEventField(event)).isEqualTo(TITLE);
    }

    @Test
    void doUpdate() {
        given(request.getTitle()).willReturn(TITLE);

        underTest.doUpdate(context, request, event);

        then(event).should().setTitle(TITLE);
    }
}