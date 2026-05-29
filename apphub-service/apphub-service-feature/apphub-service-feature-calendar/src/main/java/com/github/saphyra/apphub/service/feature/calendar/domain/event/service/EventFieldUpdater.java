package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;

import java.util.Objects;

public interface EventFieldUpdater {
    default void update(UpdateEventContext context, EventRequest request, DeprecatedEvent event) {
        Object requestField = getRequestField(request);
        Object eventField = getEventField(event);

        if (!Objects.equals(requestField, eventField)) {
            doUpdate(context, request, event);
        }
    }

    Object getRequestField(EventRequest request);

    Object getEventField(DeprecatedEvent event);

    void doUpdate(UpdateEventContext context, EventRequest request, DeprecatedEvent event);
}
