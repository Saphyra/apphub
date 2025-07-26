package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;

import java.util.Objects;

public interface EventFieldUpdater {
    default void update(UpdateEventContext context, EventRequest request, Event event) {
        if (!Objects.equals(getRequestField(request), getEventField(event))) {
            doUpdate(context, request, event);
        }
    }

    Object getRequestField(EventRequest request);

    Object getEventField(Event event);

    void doUpdate(UpdateEventContext context, EventRequest request, Event event);
}
