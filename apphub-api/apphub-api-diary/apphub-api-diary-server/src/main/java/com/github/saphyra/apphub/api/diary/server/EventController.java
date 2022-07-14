package com.github.saphyra.apphub.api.diary.server;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.CreateEventRequest;
import com.github.saphyra.apphub.api.diary.model.ReferenceDate;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface EventController {
    //TODO API test
    @PutMapping(Endpoints.DIARY_CREATE_EVENT)
    List<CalendarResponse> createEvent(@RequestBody CreateEventRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @DeleteMapping(Endpoints.DIARY_EVENT_DELETE)
    List<CalendarResponse> deleteOccurrence(@RequestBody ReferenceDate referenceDate, @PathVariable("eventId") UUID eventId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
