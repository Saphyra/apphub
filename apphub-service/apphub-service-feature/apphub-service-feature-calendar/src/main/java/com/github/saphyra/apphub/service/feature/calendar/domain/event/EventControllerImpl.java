package com.github.saphyra.apphub.service.feature.calendar.domain.event;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.api.feature.calendar.server.EventController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class EventControllerImpl implements EventController {
    private final CreateEventService createEventService;
    private final EventQueryService eventQueryService;
    private final DeleteEventService deleteEventService;
    private final EditEventService editEventService;
    private final ExpiredEventService expiredEventService;
    private final MergeEventService mergeEventService;
    private final SearchEventService searchEventService;
    private final ArchiveEventService archiveEventService;

    @Override
    public OneParamResponse<UUID> createEvent(EventRequest request, AccessToken accessToken) {
        log.info("{} wants to create an event", accessToken.getUserId());
        log.debug(request.toString());

        UUID eventId = createEventService.create(accessToken.getUserId(), request);
        OneParamResponse<UUID> response = new OneParamResponse<>(eventId);
        log.debug("Response: {}", response);

        return response;
    }

    @Override
    public List<EventResponse> getEvents(UUID label, AccessToken accessToken) {
        log.info("{} wants to know their events of label {}", accessToken.getUserId(), label);

        List<EventResponse> response = eventQueryService.getEvents(accessToken.getUserId(), label);
        log.debug("Response: {}", response);

        return response;
    }

    @Override
    public List<EventResponse> getLabellessEvents(AccessToken accessToken) {
        log.info("{} wants to know their labelless events", accessToken.getUserId());

        return eventQueryService.getLabellessEvents(accessToken.getUserId());
    }

    @Override
    public EventResponse getEvent(UUID eventId, AccessToken accessToken) {
        log.info("{} wants to get event {}", accessToken.getUserId(), eventId);

        EventResponse response = eventQueryService.getEvent(eventId);
        log.debug("Response: {}", response);

        return response;
    }

    @Override
    public void deleteEvent(UUID eventId, AccessToken accessToken) {
        log.info("{} wants to delete event {}", accessToken.getUserId(), eventId);

        deleteEventService.delete(accessToken.getUserId(), eventId);
    }

    @Override
    public void editEvent(EventRequest request, UUID eventId, AccessToken accessToken) {
        log.info("{} wants to edit event {}", accessToken.getUserId(), eventId);

        editEventService.edit(eventId, request);
    }

    @Override
    public List<EventResponse> getExpiredEvents(AccessToken accessToken) {
        log.info("{} wants to know their expired events", accessToken.getUserId());

        return expiredEventService.getExpiredEvents(accessToken.getUserId());
    }

    @Override
    public void hideExpiredEvent(UUID eventId, AccessToken accessToken) {
        log.info("{} wants to snooze expired event {}", accessToken.getUserId(), eventId);

        expiredEventService.hide(eventId);
    }

    @Override
    public void extendExpiredEvent(OneParamRequest<LocalDate> extendUntil, UUID eventId, AccessToken accessToken) {
        log.info("{} wants to extend expired event {}", accessToken.getUserId(), eventId);

        expiredEventService.extend(eventId, extendUntil.getValue());
    }

    @Override
    public void mergeEvents(UUID eventId, AccessToken accessToken) {
        log.info("{} wants to merge event {}", accessToken.getUserId(), eventId);

        mergeEventService.merge(eventId);
    }

    @Override
    public List<EventResponse> searchEvents(OneParamRequest<String> search, AccessToken accessToken) {
        log.info("{} wants to search for events", accessToken.getUserId());

        return searchEventService.search(accessToken.getUserId(), search.getValue());
    }

    @Override
    public void archiveEvent(OneParamRequest<Boolean> archive, UUID eventId, AccessToken accessToken) {
        log.info("{} wants to archive event {}", accessToken.getUserId(), eventId);

        archiveEventService.archive(eventId, archive.getValue());
    }
}
