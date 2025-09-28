package com.github.saphyra.apphub.service.calendar.domain.occurrence;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.api.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.api.calendar.server.OccurrenceController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.CreateOccurrenceService;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.DeleteOccurrenceService;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.EditOccurrenceService;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.service.OccurrenceQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class OccurrenceControllerImpl implements OccurrenceController {
    private final EditOccurrenceService editOccurrenceService;
    private final CreateOccurrenceService createOccurrenceService;
    private final OccurrenceQueryService occurrenceQueryService;
    private final DeleteOccurrenceService deleteOccurrenceService;

    @Override
    public void createOccurrence(OccurrenceRequest request, UUID eventId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create an Occurrence", accessTokenHeader.getUserId());
        log.debug("eventId: {}", eventId);
        log.debug(request.toString());

        createOccurrenceService.createOccurrence(accessTokenHeader.getUserId(), eventId, request);

        log.debug("Response: {}", HttpStatus.OK);
    }

    @Override
    public void editOccurrence(OccurrenceRequest request, UUID occurrenceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit Occurrence {}", accessTokenHeader.getUserId(), occurrenceId);
        log.debug("occurrenceId: {}", occurrenceId);
        log.debug(request.toString());

        editOccurrenceService.editOccurrence(occurrenceId, request);

        log.debug("Response: {}", HttpStatus.OK);
    }

    @Override
    public void deleteOccurrence(UUID occurrenceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete Occurrence {}", accessTokenHeader.getUserId(), occurrenceId);

        deleteOccurrenceService.deleteOccurrence(occurrenceId);

        log.debug("Response: {}", HttpStatus.OK);
    }

    @Override
    public List<OccurrenceResponse> getOccurrences(LocalDate startDate, LocalDate endDate, UUID labelId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get Occurrences for label {}", accessTokenHeader.getUserId(), labelId);
        log.info("labelId: {}, startDate: {}, endDate: {}", labelId, startDate, endDate);

        List<OccurrenceResponse> occurrences = occurrenceQueryService.getOccurrences(accessTokenHeader.getUserId(), startDate, endDate, labelId);
        log.debug("Response: {}", occurrences);

        return occurrences;
    }

    @Override
    public OccurrenceResponse getOccurrence(UUID occurrenceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get Occurrence {}", accessTokenHeader.getUserId(), occurrenceId);

        OccurrenceResponse occurrence = occurrenceQueryService.getOccurrence(occurrenceId);
        log.debug("Response: {}", occurrence);

        return occurrence;
    }

    @Override
    public List<OccurrenceResponse> getOccurrencesOfEvent(UUID eventId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get Occurrences of eventId {}", accessTokenHeader.getUserId(), eventId);

        List<OccurrenceResponse> response = occurrenceQueryService.getOccurrencesOfEvent(eventId);
        log.debug("Response: {}", response);

        return response;
    }

    @Override
    public OccurrenceResponse editOccurrenceStatus(OneParamRequest<OccurrenceStatus> status, UUID occurrenceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit status of Occurrence {}", accessTokenHeader.getUserId(), occurrenceId);
        log.debug(status.toString());

        ValidationUtil.notNull(status.getValue(), "status");

        OccurrenceResponse response = editOccurrenceService.editOccurrenceStatus(occurrenceId, status.getValue());
        log.debug("Response: {}", response);

        return response;
    }

    @Override
    public OccurrenceResponse setReminded(UUID occurrenceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to set reminded to true for Occurrence {}", accessTokenHeader.getUserId(), occurrenceId);

        OccurrenceResponse response = editOccurrenceService.setReminded(occurrenceId);
        log.debug("Response: {}", response);

        return response;
    }
}
