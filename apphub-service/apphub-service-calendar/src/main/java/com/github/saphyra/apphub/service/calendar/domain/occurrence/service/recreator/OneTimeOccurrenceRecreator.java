package com.github.saphyra.apphub.service.calendar.domain.occurrence.service.recreator;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceFactory;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.OccurrenceRecreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class OneTimeOccurrenceRecreator implements OccurrenceRecreator {
    private final OccurrenceFactory occurrenceFactory;

    @Override
    public RepetitionType getRepetitionType() {
        return RepetitionType.ONE_TIME;
    }

    @Override
    public void recreateOccurrences(UpdateEventContext context) {
        Map<LocalDate, List<Occurrence>> occurrencesByDate = context.getOccurrences()
            .stream()
            .collect(Collectors.groupingBy(Occurrence::getDate));

        Event event = context.getEvent();

        context.deleteOccurrences(occurrence -> !occurrence.getDate().equals(event.getStartDate()));

        if (!occurrencesByDate.containsKey(event.getStartDate())) {
            context.deleteOccurrences();

            Occurrence occurrence = occurrenceFactory.create(event.getUserId(), event.getEventId(), event.getStartDate(), event.getTime(), event.getRemindMeBeforeDays());
            context.addOccurrence(occurrence);
        }
    }
}
