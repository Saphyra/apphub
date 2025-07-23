package com.github.saphyra.apphub.service.calendar.domain.occurrence;

import com.github.saphyra.apphub.api.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.api.calendar.server.OccurrenceController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.CreateOccurrenceService;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.EditOccurrenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class OccurrenceControllerImpl implements OccurrenceController {
    private final EditOccurrenceService editOccurrenceService;
    private final CreateOccurrenceService createOccurrenceService;

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
}
