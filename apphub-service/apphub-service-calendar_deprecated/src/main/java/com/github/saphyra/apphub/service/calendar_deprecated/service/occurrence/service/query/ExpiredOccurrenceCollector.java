package com.github.saphyra.apphub.service.calendar_deprecated.service.occurrence.service.query;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class ExpiredOccurrenceCollector {
    private final OccurrenceDao occurrenceDao;

    public Collection<Occurrence> getExpiredOccurrences(UUID userId) {
        return occurrenceDao.getByUserId(userId)
            .stream()
            .collect(Collectors.groupingBy(Occurrence::getEventId))
            .values()
            .stream()
            .filter(o -> o.stream().anyMatch(occurrence -> occurrence.getStatus() == OccurrenceStatus.EXPIRED))
            .map(occurrenceList -> occurrenceList
                .stream()
                .filter(occurrence -> occurrence.getStatus() == OccurrenceStatus.EXPIRED)
                .min(Comparator.comparing(Occurrence::getDate))
                .orElseThrow(() -> ExceptionFactory.reportedException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, "Occurrence mapping is wrong."))
            )
            .collect(Collectors.toList());
    }
}
