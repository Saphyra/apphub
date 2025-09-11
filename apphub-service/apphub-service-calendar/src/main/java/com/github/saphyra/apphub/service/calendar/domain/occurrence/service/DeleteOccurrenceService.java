package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteOccurrenceService {
    private final OccurrenceDao occurrenceDao;

    public void deleteOccurrence(UUID occurrenceId) {
        occurrenceDao.findById(occurrenceId)
            .ifPresent(occurrenceDao::delete);
    }
}
