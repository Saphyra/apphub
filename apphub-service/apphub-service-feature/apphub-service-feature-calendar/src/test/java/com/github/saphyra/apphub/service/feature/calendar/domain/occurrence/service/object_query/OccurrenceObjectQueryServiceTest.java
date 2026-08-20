package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.common.Operation;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OccurrenceObjectQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private FindOccurrenceService findOccurrenceService;

    @Mock
    private GetOccurrencesOfUserService getOccurrencesOfUserService;

    @Mock
    private FindOccurrenceForOperationService findOccurrenceForOperationService;

    @Mock
    private GetOccurrencesOfEventService getOccurrencesOfEventService;

    @InjectMocks
    private OccurrenceObjectQueryService underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence occurrence;

    @Test
    void findOccurrence() {
        given(findOccurrenceService.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID)).willReturn(new BiWrapper<>(event, occurrence));

        assertThat(underTest.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID))
            .returns(event, BiWrapper::getEntity1)
            .returns(occurrence, BiWrapper::getEntity2);
    }

    @Test
    void getOccurrencesOfUser() {
        Map<Event, List<Occurrence>> result = Map.of(event, List.of(occurrence));
        given(getOccurrencesOfUserService.getOccurrences(USER_ID)).willReturn(result);

        assertThat(underTest.getOccurrences(USER_ID)).isSameAs(result);
    }

    @Test
    void findOccurrenceForOperation() {
        given(findOccurrenceForOperationService.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID, Operation.DELETE)).willReturn(new BiWrapper<>(event, occurrence));

        assertThat(underTest.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID, Operation.DELETE))
            .returns(event, BiWrapper::getEntity1)
            .returns(occurrence, BiWrapper::getEntity2);
    }

    @Test
    void getOccurrencesOfEvent() {
        given(getOccurrencesOfEventService.getOccurrences(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, List.of(occurrence)));

        assertThat(underTest.getOccurrences(USER_ID, EVENT_ID))
            .returns(event, BiWrapper::getEntity1)
            .returns(List.of(occurrence), BiWrapper::getEntity2);
    }
}