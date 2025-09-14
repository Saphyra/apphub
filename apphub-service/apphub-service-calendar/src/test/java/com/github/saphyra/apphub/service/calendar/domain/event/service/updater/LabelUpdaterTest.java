package com.github.saphyra.apphub.service.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.service.EventLabelMappingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LabelUpdaterTest {
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private EventLabelMappingService eventLabelMappingService;

    @InjectMocks
    private LabelUpdater underTest;

    @Mock
    private EventRequest eventRequest;

    @Mock
    private Event event;

    @Mock
    private UpdateEventContext context;

    @Test
    void getRequestField() {
        given(eventRequest.getLabels()).willReturn(List.of(LABEL_ID));

        assertThat(underTest.getRequestField(eventRequest)).isEqualTo(List.of(LABEL_ID));
    }

    @Test
    void getEventField() {
        given(event.getEventId()).willReturn(EVENT_ID);
        given(eventLabelMappingService.getLabelIds(EVENT_ID)).willReturn(List.of(LABEL_ID));

        assertThat(underTest.getEventField(event)).isEqualTo(List.of(LABEL_ID));
    }

    @Test
    void doUpdate() {
        given(event.getUserId()).willReturn(USER_ID);
        given(event.getEventId()).willReturn(EVENT_ID);
        given(eventRequest.getLabels()).willReturn(List.of(LABEL_ID));

        underTest.doUpdate(context, eventRequest, event);

        then(eventLabelMappingService).should().setLabels(USER_ID, EVENT_ID, List.of(LABEL_ID));
    }
}