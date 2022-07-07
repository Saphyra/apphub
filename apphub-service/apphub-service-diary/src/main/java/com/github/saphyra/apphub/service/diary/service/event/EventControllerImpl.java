package com.github.saphyra.apphub.service.diary.service.event;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.CreateEventRequest;
import com.github.saphyra.apphub.api.diary.server.EventController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.diary.service.event.service.DeleteEventService;
import com.github.saphyra.apphub.service.diary.service.event.service.creation.CreateEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
    public List<CalendarResponse> deleteOccurrence(OneParamRequest<LocalDate> date, UUID eventId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete event {}", accessTokenHeader.getUserId(), eventId);

        return deleteEventService.delete(accessTokenHeader.getUserId(), eventId, date.getValue());
    }
}
