package com.github.saphyra.apphub.api.calendar.server;

import com.github.saphyra.apphub.api.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.CalendarEndpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

//TODO Role protection test
//TODO API test
public interface OccurrenceController {
    @PutMapping(CalendarEndpoints.CALENDAR_CREATE_OCCURRENCE)
    void createOccurrence(@RequestBody OccurrenceRequest request, @PathVariable("eventId") UUID eventId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(CalendarEndpoints.CALENDAR_EDIT_OCCURRENCE)
    void editOccurrence(@RequestBody OccurrenceRequest request, @PathVariable("occurrenceId") UUID occurrenceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
