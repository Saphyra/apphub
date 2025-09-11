package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RecreateOccurrenceServiceTest {
    @Mock
    private OccurrenceRecreator occurrenceRecreator;

    private RecreateOccurrenceService underTest;

    @Mock
    private UpdateEventContext updateEventContext;

    @Mock
    private Event event;

    @BeforeEach
    void setUp() {
        underTest = new RecreateOccurrenceService(List.of(occurrenceRecreator));
    }

    @Test
    void recreateOccurrences() {
        given(updateEventContext.getEvent()).willReturn(event);
        given(event.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(occurrenceRecreator.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);

        underTest.recreateOccurrences(updateEventContext);

        then(occurrenceRecreator).should().recreateOccurrences(updateEventContext);
    }

    @Test
    void noRecreatorForGivenType() {
        given(updateEventContext.getEvent()).willReturn(event);
        given(event.getRepetitionType()).willReturn(RepetitionType.EVERY_X_DAYS);
        given(occurrenceRecreator.getRepetitionType()).willReturn(RepetitionType.ONE_TIME);

        assertThat(catchThrowable(() -> underTest.recreateOccurrences(updateEventContext)))
            .isInstanceOf(IllegalStateException.class);
    }
}