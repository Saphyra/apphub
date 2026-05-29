package com.github.saphyra.apphub.service.feature.calendar.common.context;

import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.RecreateOccurrenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UpdateEventContextTest {
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private DeprecatedEvent event;

    @Mock
    private DeprecatedEventDao eventDao;

    @Mock
    private DeprecatedOccurrenceDao occurrenceDao;

    @Mock
    private RecreateOccurrenceService recreateOccurrenceService;

    @InjectMocks
    private UpdateEventContext underTest;

    @Mock
    private DeprecatedOccurrence occurrence;

    @Test
    void getOccurrences(){
        given(event.getEventId()).willReturn(EVENT_ID);
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));

        assertThat(underTest.getOccurrences()).containsExactly(occurrence);
        assertThat(underTest.getOccurrences()).containsExactly(occurrence);

        then(occurrenceDao).should(times(1)).getByEventId(EVENT_ID);
    }

    @Test
    void processChanges_noOccurrenceRecreationNeeded(){
        given(event.getEventId()).willReturn(EVENT_ID);
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));
        given(occurrence.getOccurrenceId()).willReturn(OCCURRENCE_ID);

        underTest.deleteOccurrences(occ -> true);
        underTest.addOccurrence(occurrence);

        underTest.processChanges();

        then(eventDao).should().save(event);
        then(occurrenceDao).should().deleteAllById(Set.of(OCCURRENCE_ID));
        then(occurrenceDao).should().saveAll(List.of(occurrence));
        then(recreateOccurrenceService).shouldHaveNoInteractions();
    }

    @Test
    void processChanges_occurrenceRecreationNeeded(){
        given(event.getEventId()).willReturn(EVENT_ID);
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));
        given(occurrence.getOccurrenceId()).willReturn(OCCURRENCE_ID);

        underTest.deleteOccurrences(occ -> true);
        underTest.addOccurrence(occurrence);
        underTest.occurrenceRecreationNeeded();

        underTest.processChanges();

        then(eventDao).should().save(event);
        then(occurrenceDao).should().deleteAllById(Set.of(OCCURRENCE_ID));
        then(occurrenceDao).should().saveAll(List.of(occurrence));
        then(recreateOccurrenceService).should().recreateOccurrences(underTest);
    }
}