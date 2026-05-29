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
class ContentUpdaterTest {
    private static final String CONTENT = "content";

    @InjectMocks
    private ContentUpdater underTest;

    @Mock
    private EventRequest request;

    @Mock
    private DeprecatedEvent event;

    @Mock
    private UpdateEventContext context;

    @Test
    void getRequestField() {
        given(request.getContent()).willReturn(CONTENT);

        assertThat(underTest.getRequestField(request)).isEqualTo(CONTENT);
    }

    @Test
    void getEventField() {
        given(event.getContent()).willReturn(CONTENT);

        assertThat(underTest.getEventField(event)).isEqualTo(CONTENT);
    }

    @Test
    void doUpdate() {
        given(request.getContent()).willReturn(CONTENT);

        underTest.doUpdate(context, request, event);

        then(event).should().setContent(CONTENT);
    }
}