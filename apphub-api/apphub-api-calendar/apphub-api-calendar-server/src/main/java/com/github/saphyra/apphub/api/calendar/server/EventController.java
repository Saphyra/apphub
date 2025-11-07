package com.github.saphyra.apphub.api.calendar.server;

import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.api.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.endpoints.CalendarEndpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface EventController {
    @PutMapping(CalendarEndpoints.CALENDAR_CREATE_EVENT)
    OneParamResponse<UUID> createEvent(@RequestBody EventRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(CalendarEndpoints.CALENDAR_GET_EVENTS)
    List<EventResponse> getEvents(
        @RequestParam(name = "labelId", required = false) UUID label,
        @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader
    );

    @GetMapping(CalendarEndpoints.CALENDAR_LABELLESS_GET_EVENTS)
    List<EventResponse> getLabellessEvents(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(CalendarEndpoints.CALENDAR_GET_EVENT)
    EventResponse getEvent(@PathVariable("eventId") UUID eventId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(CalendarEndpoints.CALENDAR_DELETE_EVENT)
    void deleteEvent(@PathVariable("eventId") UUID eventId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(CalendarEndpoints.CALENDAR_EDIT_EVENT)
    void editEvent(@RequestBody EventRequest request, @PathVariable("eventId") UUID eventId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
