package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;

import java.util.Objects;

public interface EventFieldUpdater {
    default void update(UpdateEventContext context, EventRequest request, Event event) {
        Object requestField = getRequestField(request);
        Object eventField = getEventField(event);

        if (!Objects.equals(requestField, eventField)) {
            doUpdate(context, request, event);
        }
    }

    Object getRequestField(EventRequest request);

    Object getEventField(Event event);

    void doUpdate(UpdateEventContext context, EventRequest request, Event event);
}
