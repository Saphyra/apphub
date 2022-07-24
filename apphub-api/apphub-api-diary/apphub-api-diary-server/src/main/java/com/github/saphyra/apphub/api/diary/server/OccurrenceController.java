package com.github.saphyra.apphub.api.diary.server;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.EditOccurrenceRequest;
import com.github.saphyra.apphub.api.diary.model.ReferenceDate;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface OccurrenceController {
    @PostMapping(Endpoints.DIARY_OCCURRENCE_EDIT)
    List<CalendarResponse> editOccurrence(@RequestBody EditOccurrenceRequest request, @PathVariable("occurrenceId") UUID occurrenceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.DIARY_OCCURRENCE_DONE)
    List<CalendarResponse> markOccurrenceDone(@RequestBody ReferenceDate referenceDate, @PathVariable("occurrenceId") UUID occurrenceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.DIARY_OCCURRENCE_SNOOZED)
    List<CalendarResponse> markOccurrenceSnoozed(@RequestBody ReferenceDate referenceDate, @PathVariable("occurrenceId") UUID occurrenceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.DIARY_OCCURRENCE_DEFAULT)
    List<CalendarResponse> markOccurrenceDefault(@RequestBody ReferenceDate referenceDate, @PathVariable("occurrenceId") UUID occurrenceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
