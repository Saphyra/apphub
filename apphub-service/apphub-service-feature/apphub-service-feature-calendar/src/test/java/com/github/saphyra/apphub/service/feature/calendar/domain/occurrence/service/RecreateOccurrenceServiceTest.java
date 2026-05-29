package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
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
    private DeprecatedEvent event;

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