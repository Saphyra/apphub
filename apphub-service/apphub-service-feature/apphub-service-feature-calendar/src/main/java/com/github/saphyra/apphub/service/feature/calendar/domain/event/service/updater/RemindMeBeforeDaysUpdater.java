package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.EventFieldUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class RemindMeBeforeDaysUpdater implements EventFieldUpdater {
    @Override
    public Object getRequestField(EventRequest request) {
        return request.getRemindMeBeforeDays();
    }

    @Override
    public Object getEventField(Event event) {
        return event.getRemindMeBeforeDays();
    }

    @Override
    public void doUpdate(UpdateEventContext context, EventRequest request, Event event) {
        log.info("Updating remindMeBeforeDays of event {}", event.getEventId());

        event.setRemindMeBeforeDays(request.getRemindMeBeforeDays());
    }
}
