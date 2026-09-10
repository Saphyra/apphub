package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.updater;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.EventFieldUpdater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class AutoDoneUpdater implements EventFieldUpdater {
    @Override
    public Object getRequestField(EventRequest request) {
        return request.getAutoDone();
    }

    @Override
    public Object getEventField(Event event) {
        return event.isAutoDone();
    }

    @Override
    public void doUpdate(UpdateEventContext context, EventRequest request, Event event) {
        log.info("Updating autoDone of Event {}", event.getEventId());

        event.setAutoDone(request.getAutoDone());
    }
}
