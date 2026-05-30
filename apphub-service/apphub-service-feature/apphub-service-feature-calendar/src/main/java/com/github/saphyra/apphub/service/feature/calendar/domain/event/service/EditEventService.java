package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContextFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import jakarta.transaction.Transactional;
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
    private final EventDao eventDao;
    private final UpdateEventContextFactory updateEventContextFactory;
    private final List<EventFieldUpdater> eventFieldUpdaters;

    @Transactional
    public void edit(UUID userId, UUID eventId, EventRequest request) {
        eventRequestValidator.validateEdit(userId, request);

        Event event = eventDao.findByIdValidated(userId, eventId);
        event.setExpirationNotified(false);
        UpdateEventContext context = updateEventContextFactory.create(event);

        log.info("Updating fields of DeprecatedEvent {}", eventId);
        eventFieldUpdaters.forEach(eventFieldUpdater -> eventFieldUpdater.update(context, request, event));
        log.info("Updating fields of DeprecatedEvent {} finished", eventId);

        context.processChanges();
    }
}
