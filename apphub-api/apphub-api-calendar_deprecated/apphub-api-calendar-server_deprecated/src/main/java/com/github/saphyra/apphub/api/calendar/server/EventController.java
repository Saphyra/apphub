package com.github.saphyra.apphub.api.calendar.server;

import com.github.saphyra.apphub.api.calendar.model.CalendarResponse;
import com.github.saphyra.apphub.api.calendar.model.CreateEventRequest;
import com.github.saphyra.apphub.api.calendar.model.ReferenceDate;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.CalendarEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface EventController {
    @PutMapping(CalendarEndpoints.CALENDAR_CREATE_EVENT)
    List<CalendarResponse> createEvent(@RequestBody CreateEventRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(CalendarEndpoints.CALENDAR_EVENT_DELETE)
    List<CalendarResponse> deleteEvent(@RequestBody ReferenceDate referenceDate, @PathVariable("eventId") UUID eventId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
