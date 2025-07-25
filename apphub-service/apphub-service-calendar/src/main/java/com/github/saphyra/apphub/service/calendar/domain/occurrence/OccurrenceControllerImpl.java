package com.github.saphyra.apphub.service.calendar.domain.occurrence;

import com.github.saphyra.apphub.api.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.api.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.api.calendar.server.OccurrenceController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.CreateOccurrenceService;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.EditOccurrenceService;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.OccurrenceQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class OccurrenceControllerImpl implements OccurrenceController {
    private final EditOccurrenceService editOccurrenceService;
    private final CreateOccurrenceService createOccurrenceService;
    private final OccurrenceQueryService occurrenceQueryService;

    @Override
    public void createOccurrence(OccurrenceRequest request, UUID eventId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create an Occurrence", accessTokenHeader.getUserId());

        createOccurrenceService.createOccurrence(accessTokenHeader.getUserId(), eventId, request);
    }

    @Override
    public void editOccurrence(OccurrenceRequest request, UUID occurrenceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit Occurrence {}", accessTokenHeader.getUserId(), occurrenceId);

        editOccurrenceService.editOccurrence(occurrenceId, request);
    }

    @Override
    public List<OccurrenceResponse> getOccurrences(LocalDate startDate, LocalDate endDate, UUID labelId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get Occurrences", accessTokenHeader.getUserId());

        return occurrenceQueryService.getOccurrences(accessTokenHeader.getUserId(), startDate, endDate, labelId);
    }
}
