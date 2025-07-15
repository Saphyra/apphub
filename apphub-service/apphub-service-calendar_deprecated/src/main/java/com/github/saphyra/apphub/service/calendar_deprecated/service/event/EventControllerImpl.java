package com.github.saphyra.apphub.service.calendar_deprecated.service.event;

import com.github.saphyra.apphub.api.calendar.model.CalendarResponse;
import com.github.saphyra.apphub.api.calendar.model.CreateEventRequest;
import com.github.saphyra.apphub.api.calendar.model.ReferenceDate;
import com.github.saphyra.apphub.api.calendar.server.EventController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.calendar_deprecated.service.event.service.DeleteEventService;
import com.github.saphyra.apphub.service.calendar_deprecated.service.event.service.creation.CreateEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventControllerImpl implements EventController {
    private final CreateEventService createEventService;
    private final DeleteEventService deleteEventService;

    @Override
    public List<CalendarResponse> createEvent(CreateEventRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a new event.", accessTokenHeader.getUserId());
        return createEventService.createEvent(accessTokenHeader.getUserId(), request);
    }

    @Override
    public List<CalendarResponse> deleteEvent(ReferenceDate referenceDate, UUID eventId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete event {}", accessTokenHeader.getUserId(), eventId);

        return deleteEventService.delete(accessTokenHeader.getUserId(), eventId, referenceDate);
    }
}
