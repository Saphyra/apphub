package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.diary.dao.event.EventDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
public class OccurrenceQueryService {
    private final OccurrenceFetcher occurrenceFetcher;
    private final EventDao eventDao;
    private final OccurrenceDao occurrenceDao;
    private final DateTimeUtil dateTimeUtil;
    private final ExpiredOccurrenceCollector expiredOccurrenceCollector;

    @Transactional
    public Map<LocalDate, List<Occurrence>> getOccurrences(UUID userId, Collection<LocalDate> dates) {
        List<LocalDate> sortedDates = dates.stream()
            .sorted()
            .collect(Collectors.toList());

        LocalDate currentDate = dateTimeUtil.getCurrentDate();

        List<Occurrence> occurrences = eventDao.getByUserId(userId).stream()
            .flatMap(event -> occurrenceFetcher.fetchOccurrencesOfEvent(event, sortedDates).stream())
            .collect(Collectors.toList());
        Map<LocalDate, List<Occurrence>> occurrenceMapping = occurrences.stream()
            .peek(occurrence -> {
                if (occurrence.getDate().isBefore(currentDate) && (occurrence.getStatus() == OccurrenceStatus.VIRTUAL || occurrence.getStatus() == OccurrenceStatus.PENDING)) {
                    log.debug("{} is expired.", occurrence);
                    occurrence.setStatus(OccurrenceStatus.EXPIRED);
                    occurrenceDao.save(occurrence);
                }
            })
            .collect(Collectors.groupingBy(Occurrence::getDate));

        if (sortedDates.contains(currentDate)) {
            List<Occurrence> currentDateOccurrences = occurrenceMapping.getOrDefault(currentDate, new ArrayList<>());
            currentDateOccurrences.addAll(expiredOccurrenceCollector.getExpiredOccurrences(userId));
            occurrenceMapping.put(currentDate, currentDateOccurrences);
        }

        return sortedDates.stream()
            .collect(Collectors.toMap(Function.identity(), date -> occurrenceMapping.getOrDefault(date, Collections.emptyList())));
    }
}
