package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeleteOccurrenceServiceTest {
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private OccurrenceDao occurrenceDao;

    @InjectMocks
    private DeleteOccurrenceService underTest;

    @Mock
    private Occurrence occurrence;

    @Test
    void deleteOccurrence() {
        given(occurrenceDao.findByIdValidated(EVENT_ID, OCCURRENCE_ID)).willReturn(occurrence);

        underTest.deleteOccurrence(EVENT_ID, OCCURRENCE_ID);

        then(occurrenceDao).should().delete(List.of(occurrence));
    }
}