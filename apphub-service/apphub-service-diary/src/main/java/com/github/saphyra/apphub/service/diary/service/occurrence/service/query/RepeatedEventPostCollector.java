package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceType;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.OccurrenceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class RepeatedEventPostCollector {
    private final OccurrenceFactory occurrenceFactory;
    private final OccurrenceDao occurrenceDao;

    List<Occurrence> collect(Event event, List<Occurrence> occurrences, List<LocalDate> dates) {
        int numberOfFollowUpOccurrences = event.getRepeat();
        log.debug("Number of follow-up occurrences: {}", numberOfFollowUpOccurrences - 1);

        List<Occurrence> result = new ArrayList<>(occurrences);

        List<Occurrence> processableOccurrences = occurrences.stream()
            .filter(occurrence -> occurrence.getType() != OccurrenceType.FOLLOW_UP)
            .collect(Collectors.toList());
        log.debug("Input occurrences: {}", processableOccurrences);

        processableOccurrences.forEach(occurrence -> {
            log.debug("Processing occurrence {}", occurrence);

            for (int i = 1; i < numberOfFollowUpOccurrences; i++) {
                LocalDate followUpDate = occurrence.getDate().plusDays(i);
                log.info("Follow-up date: {}", followUpDate);

                if (!dates.contains(followUpDate)) {
                    log.debug("FollowUp date is not in the list. Skipping...");
                    continue;
                }
                Occurrence followUpOccurrence = occurrences.stream()
                    .filter(o -> o.getDate().equals(followUpDate))
                    .findAny()
                    .orElseGet(() -> createOccurrence(event, followUpDate));
                log.debug("Occurrence found: {}", followUpOccurrence);

                result.add(followUpOccurrence);
            }
        });

        log.debug("Result: {}", result);
        return result.stream()
            .distinct()
            .collect(Collectors.toList());
    }

    private Occurrence createOccurrence(Event event, LocalDate date) {
        log.debug("Follow-up occurrence not found on {}", date);
        Occurrence occurrence = occurrenceFactory.createVirtual(date, event, OccurrenceType.FOLLOW_UP);
        occurrenceDao.save(occurrence);
        log.debug("Follow-up occurrence created: {}", occurrence);
        return occurrence;
    }
}
