package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.calendar.common.context.UpdateEventContextFactory;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
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
    public void edit(UUID eventId, EventRequest request) {
        eventRequestValidator.validate(request);

        Event event = eventDao.findByIdValidated(eventId);
        UpdateEventContext context = updateEventContextFactory.create(event);

        log.info("Updating fields of Event {}", eventId);
        eventFieldUpdaters.forEach(eventFieldUpdater -> eventFieldUpdater.update(context, request, event));
        log.info("Updating fields of Event {} finished", eventId);

        context.processChanges();
    }
}
