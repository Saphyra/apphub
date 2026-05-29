package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteOccurrenceService {
    private final DeprecatedOccurrenceDao occurrenceDao;

    public void deleteOccurrence(UUID occurrenceId) {
        occurrenceDao.findById(occurrenceId)
            .ifPresent(occurrenceDao::delete);
    }
}
