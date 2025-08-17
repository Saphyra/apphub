package com.github.saphyra.apphub.service.calendar.migration;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.condition.RepetitionTypeConditionSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class OccurrenceMigrator {
    private final MigrationDao migrationDao;
    private final RepetitionTypeConditionSelector repetitionTypeConditionSelector;
    private final DateTimeUtil dateTimeUtil;
    private final OccurrenceDao occurrenceDao;
    private final IdGenerator idGenerator;

    void migrate(Event event) {
        log.info("Migrating occurrences for event {}", event.getEventId());

        LocalDate currentDate = dateTimeUtil.getCurrentDate();

        Map<LocalDate, List<DeprecatedOccurrence>> occurrences = migrationDao.getOccurrences(event.getEventId())
            .stream()
            .collect(Collectors.groupingBy(DeprecatedOccurrence::getDate));
        log.info("Found {} occurrences for event {}", occurrences.size(), event.getEventId());

        List<LocalDate> dates = repetitionTypeConditionSelector.get(event.getRepetitionType(), event.getRepetitionData())
            .getOccurrences(event.getStartDate(), event.getEndDate(), event.getRepeatForDays(), null);

        dates.forEach(date -> {
            List<DeprecatedOccurrence> existingOccurrences = occurrences.get(date);
            if (date.isBefore(currentDate)) {
                if (nonNull(existingOccurrences)) {
                    existingOccurrences.stream()
                        .filter(deprecatedOccurrence -> deprecatedOccurrence.getStatus() != OccurrenceStatus.PENDING)
                        .map(this::convert)
                        .forEach(occurrenceDao::save);
                } else {
                    //Occurrence creation is not needed for past dates
                }
            } else {
                if (nonNull(existingOccurrences)) {
                    existingOccurrences.stream()
                        .filter(deprecatedOccurrence -> deprecatedOccurrence.getStatus() != OccurrenceStatus.PENDING)
                        .map(this::convert)
                        .forEach(occurrenceDao::save);
                } else {
                    Occurrence occurrence = Occurrence.builder()
                        .occurrenceId(idGenerator.randomUuid())
                        .userId(event.getUserId())
                        .eventId(event.getEventId())
                        .date(date)
                        .status(OccurrenceStatus.PENDING)
                        .build();
                    occurrenceDao.save(occurrence);
                }
            }
        });

        log.info("Migration finished for event {}.", event.getEventId());
    }

    private Occurrence convert(DeprecatedOccurrence deprecatedOccurrence) {
        return Occurrence.builder()
            .occurrenceId(deprecatedOccurrence.getOccurrenceId())
            .userId(deprecatedOccurrence.getUserId())
            .eventId(deprecatedOccurrence.getEventId())
            .date(deprecatedOccurrence.getDate())
            .status(deprecatedOccurrence.getStatus())
            .note(deprecatedOccurrence.getNote())
            .build();
    }
}
