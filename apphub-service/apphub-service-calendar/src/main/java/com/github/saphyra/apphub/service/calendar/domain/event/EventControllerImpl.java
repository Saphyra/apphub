package com.github.saphyra.apphub.service.calendar.domain.event;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.api.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.api.calendar.server.EventController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.calendar.domain.event.service.CreateEventService;
import com.github.saphyra.apphub.service.calendar.domain.event.service.DeleteEventService;
import com.github.saphyra.apphub.service.calendar.domain.event.service.EditEventService;
import com.github.saphyra.apphub.service.calendar.domain.event.service.EventQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EventControllerImpl implements EventController {
    private final CreateEventService createEventService;
    private final EventQueryService eventQueryService;
    private final DeleteEventService deleteEventService;
    private final EditEventService editEventService;

    @Override
    public void createEvent(EventRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create an event", accessTokenHeader.getUserId());

        createEventService.create(accessTokenHeader.getUserId(), request);
    }

    @Override
    public List<EventResponse> getEvents(UUID label, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know their events", accessTokenHeader.getUserId());

        return eventQueryService.getEvents(accessTokenHeader.getUserId(), label);
    }

    @Override
    public EventResponse getEvent(UUID eventId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get event {}", accessTokenHeader.getUserId(), eventId);

        return eventQueryService.getEvent(eventId);
    }

    @Override
    public void deleteEvent(UUID eventId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete event {}", accessTokenHeader.getUserId(), eventId);

        deleteEventService.delete(accessTokenHeader.getUserId(), eventId);
    }

    @Override
    public void editEvent(EventRequest request, UUID eventId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit event {}", accessTokenHeader.getUserId(), eventId);

        editEventService.edit(accessTokenHeader.getUserId(), eventId, request);
    }
}
