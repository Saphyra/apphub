package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.recreator;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceFactory;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.OccurrenceRecreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EveryXDaysOccurrenceRecreator implements OccurrenceRecreator {
    private final DateTimeUtil dateTimeUtil;
    private final OccurrenceFactory occurrenceFactory;

    @Override
    public RepetitionType getRepetitionType() {
        return RepetitionType.EVERY_X_DAYS;
    }

    @Override
    public void recreateOccurrences(UpdateEventContext context) {
        Event event = context.getEvent();

        Map<LocalDate, List<Occurrence>> existingOccurrences = context.getOccurrences()
            .stream()
            .collect(Collectors.groupingBy(Occurrence::getDate));

        int days = Integer.parseInt(event.getRepetitionData());

        LocalDate startDate = dateTimeUtil.earlier(event.getStartDate(), dateTimeUtil.earliest(existingOccurrences.keySet()));
        LocalDate endDate = dateTimeUtil.later(event.getStartDate(), dateTimeUtil.latest(existingOccurrences.keySet()));

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            boolean shouldHaveOccurrence = ChronoUnit.DAYS.between(startDate, date) % days == 0;
            boolean outOfRange = date.isBefore(event.getStartDate()) || date.isAfter(event.getEndDate());
            Optional<List<Occurrence>> occurrencesOnDate = Optional.ofNullable(existingOccurrences.get(date));

            if (outOfRange) {
                //Delete any occurrences that are out of range
                occurrencesOnDate.ifPresent(context::deleteOccurrences);
            } else if (shouldHaveOccurrence && occurrencesOnDate.isEmpty()) {
                Occurrence occurrence = occurrenceFactory.create(event.getUserId(), event.getEventId(), date, event.getTime(), event.getRemindMeBeforeDays());
                context.addOccurrence(occurrence);
            } else if (!shouldHaveOccurrence && occurrencesOnDate.isPresent()) {
                //Delete occurrences that should not be there
                context.deleteOccurrences(occurrencesOnDate.get());
            }
        }
    }
}
