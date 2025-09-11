package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeleteOccurrenceServiceTest {
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private OccurrenceDao occurrenceDao;

    @InjectMocks
    private DeleteOccurrenceService underTest;

    @Mock
    private Occurrence occurrence;

    @Test
    void deleteOccurrence() {
        given(occurrenceDao.findById(OCCURRENCE_ID)).willReturn(Optional.of(occurrence));

        underTest.deleteOccurrence(OCCURRENCE_ID);

        then(occurrenceDao).should().delete(occurrence);
    }
}