package com.github.saphyra.apphub.service.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.service.EventFieldUpdater;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class RepeatForDaysUpdater implements EventFieldUpdater {
    @Override
    public Object getRequestField(EventRequest request) {
        return request.getRepeatForDays();
    }

    @Override
    public Object getEventField(Event event) {
        return event.getRepeatForDays();
    }

    @Override
    public void doUpdate(UpdateEventContext context, EventRequest request, Event event) {
        log.info("Updating repeatForDays of Event {}", event.getEventId());

        event.setRepeatForDays(request.getRepeatForDays());

        context.occurrenceRecreationNeeded();
    }
}
