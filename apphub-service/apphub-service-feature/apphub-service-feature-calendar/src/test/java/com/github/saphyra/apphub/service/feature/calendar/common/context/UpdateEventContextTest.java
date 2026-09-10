package com.github.saphyra.apphub.service.feature.calendar.common.context;

import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.RecreateOccurrenceService;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UpdateEventContextTest {
    private static final @NonNull UUID EVENT_ID = UUID.randomUUID();
    private static final @NonNull UUID DELETED_OCCURRENCE_ID = UUID.randomUUID();
    private static final @NonNull UUID MODIFIED_OCCURRENCE_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final @NonNull UUID USER_ID = UUID.randomUUID();

    @Mock
    private CommonCalendarDao commonCalendarDao;

    @Mock
    private RecreateOccurrenceService recreateOccurrenceService;

    @Mock
    private Event event;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private EventDao eventDao;

    private UpdateEventContext underTest;

    @Mock
    private Occurrence deletedOccurrence;

    @Mock
    private Occurrence modifiedOccurrence;

    @BeforeEach
    void setUp(){
        given(event.getEventId()).willReturn(EVENT_ID);
        given(commonCalendarDao.getOccurrenceDao()).willReturn(occurrenceDao);
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(deletedOccurrence));
        given(deletedOccurrence.getOccurrenceId()).willReturn(DELETED_OCCURRENCE_ID);
        given(modifiedOccurrence.getOccurrenceId()).willReturn(MODIFIED_OCCURRENCE_ID);

        underTest = new UpdateEventContext(event, commonCalendarDao, recreateOccurrenceService);
    }

    @Test
    void processChanges_occurrenceRecreationNeeded() {
        underTest.deleteOccurrences(occurrence -> occurrence.getOccurrenceId().equals(DELETED_OCCURRENCE_ID));
        underTest.addOccurrence(modifiedOccurrence);
        underTest.occurrenceRecreationNeeded();
        given(commonCalendarDao.getEventDao()).willReturn(eventDao);

        underTest.processChanges();

        then(recreateOccurrenceService).should().recreateOccurrences(underTest);
        then(eventDao).should().save(event);
        then(occurrenceDao).should().delete(EVENT_ID, Set.of(DELETED_OCCURRENCE_ID));
        then(occurrenceDao).should().save(List.of(modifiedOccurrence));
    }

    @Test
    void processChangesWithLabels_noOccurrenceRecreation(){
        underTest.deleteOccurrences(occurrence -> occurrence.getOccurrenceId().equals(DELETED_OCCURRENCE_ID));
        underTest.addOccurrence(modifiedOccurrence);
        given(commonCalendarDao.getEventDao()).willReturn(eventDao);
        given(event.getUserId()).willReturn(USER_ID);

        underTest.processChanges(Map.of(LABEL_ID, USER_ID));

        then(recreateOccurrenceService).should(never()).recreateOccurrences(any());
        then(eventDao).should().save(event);
        then(occurrenceDao).should().delete(EVENT_ID, Set.of(DELETED_OCCURRENCE_ID));
        then(occurrenceDao).should().save(List.of(modifiedOccurrence));
        then(commonCalendarDao).should().editLabelsOfEvent(USER_ID, EVENT_ID, Map.of(LABEL_ID, USER_ID));
    }
}