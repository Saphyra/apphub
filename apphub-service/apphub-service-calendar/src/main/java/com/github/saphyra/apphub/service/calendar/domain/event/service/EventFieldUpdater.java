package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

public interface EventFieldUpdater {
    default void update(UpdateEventContext context, EventRequest request, Event event) {
        Object requestField = getRequestField(request);
        LogHolder.log.info("{} - requestField: {}", getClass().getSimpleName(), requestField); //TODO remove
        Object eventField = getEventField(event);
        LogHolder.log.info("{} - eventField: {}", getClass().getSimpleName(), eventField);  //TODO remove

        if (!Objects.equals(requestField, eventField)) {
            LogHolder.log.info("{} - Update needed.", getClass().getSimpleName());

            doUpdate(context, request, event);
        }
    }

    Object getRequestField(EventRequest request);

    Object getEventField(Event event);

    void doUpdate(UpdateEventContext context, EventRequest request, Event event);

    @Slf4j
    class LogHolder {
    }
}
