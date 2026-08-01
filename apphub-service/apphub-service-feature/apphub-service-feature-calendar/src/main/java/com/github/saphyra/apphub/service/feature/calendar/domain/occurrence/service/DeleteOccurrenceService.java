package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteOccurrenceService {
    private final OccurrenceDao occurrenceDao;
    private final AlmDao almDao;

    public void deleteOccurrence(UUID eventId, UUID occurrenceId) {
        Occurrence occurrence = occurrenceDao.findByIdValidated(eventId, occurrenceId); //Query to validate ownership
        occurrenceDao.delete(List.of(occurrence));

        almDao.deleteByObject(occurrenceId, SharedObjectType.OCCURRENCE);
    }
}
