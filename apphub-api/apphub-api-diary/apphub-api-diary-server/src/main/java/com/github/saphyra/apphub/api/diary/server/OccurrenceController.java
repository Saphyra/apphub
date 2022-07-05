package com.github.saphyra.apphub.api.diary.server;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.EditOccurrenceRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface OccurrenceController {
    //TODO API test
    @PostMapping(Endpoints.DIARY_OCCURRENCE_EDIT)
    CalendarResponse editOccurrence(@RequestBody EditOccurrenceRequest request, @PathVariable("occurrenceId") UUID occurrenceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @PostMapping(Endpoints.DIARY_OCCURRENCE_DONE)
    CalendarResponse markOccurrenceDone(@PathVariable("occurrenceId") UUID occurrenceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @PostMapping(Endpoints.DIARY_OCCURRENCE_SNOOZED)
    CalendarResponse markOccurrenceSnoozed(@PathVariable("occurrenceId") UUID occurrenceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @PostMapping(Endpoints.DIARY_OCCURRENCE_DEFAULT)
    CalendarResponse markOccurrenceDefault(@PathVariable("occurrenceId") UUID occurrenceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    //TODO API test
    @DeleteMapping(Endpoints.DIARY_OCCURRENCE_DELETE)
    List<CalendarResponse> deleteOccurrence(@PathVariable("occurrenceId") UUID occurrenceId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
