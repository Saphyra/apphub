package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContext;
import com.github.saphyra.apphub.service.feature.calendar.common.context.UpdateEventContextFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
//TODO unit test
public class EditEventService {
    private final EventRequestValidator eventRequestValidator;
    private final EventDao eventDao;
    private final UpdateEventContextFactory updateEventContextFactory;
    private final List<EventFieldUpdater> eventFieldUpdaters;
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;

    public void edit(UUID userId, UUID eventId, EventRequest request) {
        eventRequestValidator.validateEdit(request);

        Event event = eventDao.findById(userId, eventId)
            .or(() -> almDao.findForObject(userId, PrincipalType.USER, eventId, SharedObjectType.EVENT).flatMap(alm -> eventDao.findById(alm.getOwner(), eventId)))
            .or(() -> getEventOfSharedLabel(userId, eventId))
            .orElseThrow(() -> ExceptionFactory.notFound("Event " + eventId + " does not exist or not available for user " + userId));
        event.setExpirationNotified(false);
        UpdateEventContext context = updateEventContextFactory.create(event);

        log.info("Updating fields of DeprecatedEvent {}", eventId);
        eventFieldUpdaters.forEach(eventFieldUpdater -> eventFieldUpdater.update(context, request, event));
        log.info("Updating fields of DeprecatedEvent {} finished", eventId);

        context.processChanges(request.getLabels());
    }

    //TODO verify access
    private Optional<Event> getEventOfSharedLabel(UUID userId, UUID eventId) {
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            .map(alm -> eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId()))
            .flatMap(mapping -> mapping.getEventIds().entrySet().stream())
            .filter(mapping -> mapping.getKey().equals(eventId))
            .findFirst()
            .flatMap(mapping -> eventDao.findById(mapping.getValue(), mapping.getKey()));
    }
}
