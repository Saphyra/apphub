package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.diary.dao.event.EventDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
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
//TODO unit test
public class OccurrenceQueryService {
    private final OccurrenceFetcher occurrenceFetcher;
    private final EventDao eventDao;
    private final OccurrenceDao occurrenceDao;
    private final DateTimeUtil dateTimeUtil;

    @Transactional
    public Map<LocalDate, List<Occurrence>> getOccurrences(UUID userId, Collection<LocalDate> dates) {
        List<LocalDate> sortedDates = dates.stream()
            .sorted()
            .collect(Collectors.toList());

        LocalDate currentDate = dateTimeUtil.getCurrentDate();

        Map<LocalDate, List<Occurrence>> occurrences = eventDao.getByUserId(userId)
            .stream()
            .flatMap(event -> occurrenceFetcher.fetchOccurrenceOfEvent(event, sortedDates).stream())
            .peek(occurrence -> {
                if (occurrence.getDate().isBefore(currentDate) && (occurrence.getStatus() == OccurrenceStatus.VIRTUAL || occurrence.getStatus() == OccurrenceStatus.PENDING)) {
                    log.debug("{} is expired.", occurrence);
                    occurrence.setStatus(OccurrenceStatus.EXPIRED);
                    occurrenceDao.save(occurrence);
                }
            })
            .collect(Collectors.groupingBy(Occurrence::getDate));
        return sortedDates.stream()
            .collect(Collectors.toMap(Function.identity(), date -> occurrences.getOrDefault(date, Collections.emptyList())));
    }
}
