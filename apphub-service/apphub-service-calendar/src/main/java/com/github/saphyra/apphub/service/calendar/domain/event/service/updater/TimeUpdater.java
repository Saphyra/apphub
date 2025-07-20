package com.github.saphyra.apphub.service.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.service.EventFieldUpdater;
import com.github.saphyra.apphub.service.calendar.domain.event.service.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class TimeUpdater implements EventFieldUpdater {
    private final DateTimeUtil dateTimeUtil;

    @Override
    public Object getRequestField(EventRequest request) {
        return request.getTime();
    }

    @Override
    public Object getEventField(Event event) {
        return event.getTime();
    }

    @Override
    public void doUpdate(UpdateEventContext context, EventRequest request, Event event) {
        log.info("Updating time of event {}", event.getEventId());

        LocalDate currentDate = dateTimeUtil.getCurrentDate();

        event.setTime(request.getTime());

        List<Occurrence> modifiedOccurrences = context.getOccurrences()
            .stream()
            .filter(occurrence -> !occurrence.getDate().isBefore(currentDate))
            .toList();

        modifiedOccurrences.forEach(occurrence -> occurrence.setTime(request.getTime()));

        context.occurrencesModified(modifiedOccurrences);
    }
}
