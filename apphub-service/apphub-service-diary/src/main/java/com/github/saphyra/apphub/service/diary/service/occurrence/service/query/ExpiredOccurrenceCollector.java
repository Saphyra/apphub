package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class ExpiredOccurrenceCollector {
    public Collection<Occurrence> getExpiredOccurrences(List<Occurrence> occurrences) {
        return occurrences.stream()
            .collect(Collectors.groupingBy(Occurrence::getEventId))
            .values()
            .stream()
            .map(occurrenceList -> occurrenceList
                .stream()
                .min(Comparator.comparing(Occurrence::getDate))
                .orElseThrow(() -> ExceptionFactory.reportedException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR, "Occurrence mapping is wrong."))
            )
            .filter(occurrence -> occurrence.getStatus() == OccurrenceStatus.EXPIRED)
            .collect(Collectors.toList());
    }
}
