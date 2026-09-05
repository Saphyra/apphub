package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContextFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query.EventObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.common.Operation;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
public class EditEventService {
    private final EventRequestValidator eventRequestValidator;
    private final UpdateEventContextFactory updateEventContextFactory;
    private final List<EventFieldUpdater> eventFieldUpdaters;
    private final EventObjectQueryService eventObjectQueryService;

    public void edit(UUID userId, UUID eventId, EventRequest request) {
        eventRequestValidator.validateEdit(request);

        Event event = eventObjectQueryService.findEvent(userId, eventId, Operation.EDIT)
            .orElseThrow(() -> ExceptionFactory.notFound("Event " + eventId + " does not exist or not available for user " + userId));

        event.setExpirationNotified(false);
        UpdateEventContext context = updateEventContextFactory.create(event);

        log.info("Updating fields of Event {}", eventId);
        eventFieldUpdaters.forEach(eventFieldUpdater -> eventFieldUpdater.update(context, request, event));
        log.info("Updating fields of Event {} finished", eventId);

        context.processChanges(request.getLabels());
    }
}
