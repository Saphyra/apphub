package com.github.saphyra.apphub.service.diary.service.event;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.CreateEventRequest;
import com.github.saphyra.apphub.api.diary.server.EventController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.diary.service.event.service.creation.CreateEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventControllerImpl implements EventController {
    private final CreateEventService createEventService;

    @Override
    public CalendarResponse createEvent(CreateEventRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a new event.", accessTokenHeader.getUserId());
        return createEventService.createEvent(accessTokenHeader.getUserId(), request);
    }
}
