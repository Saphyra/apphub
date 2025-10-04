package com.github.saphyra.apphub.service.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
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
class RepetitionDataUpdaterTest {
    private static final String REPETITION_DATA = "repetition-data";
    private static final String STRINGIFIED_REPETITION_DATA = "stringified-repetition-data";

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private RepetitionDataUpdater underTest;

    @Mock
    private EventRequest request;

    @Mock
    private Event event;

    @Mock
    private UpdateEventContext context;

    @Test
    void getRequestField() {
        given(request.getRepetitionData()).willReturn(REPETITION_DATA);

        assertThat(underTest.getRequestField(request)).isEqualTo(REPETITION_DATA);
    }

    @Test
    void getEventField() {
        given(event.getRepetitionData()).willReturn(REPETITION_DATA);

        assertThat(underTest.getEventField(event)).isEqualTo(REPETITION_DATA);
    }

    @Test
    void doUpdate() {
        given(request.getRepetitionData()).willReturn(REPETITION_DATA);
        given(objectMapperWrapper.writeValueAsPrettyString(REPETITION_DATA)).willReturn(STRINGIFIED_REPETITION_DATA);

        underTest.doUpdate(context, request, event);

        then(event).should().setRepetitionData(STRINGIFIED_REPETITION_DATA);
        then(context).should().occurrenceRecreationNeeded();
    }
}