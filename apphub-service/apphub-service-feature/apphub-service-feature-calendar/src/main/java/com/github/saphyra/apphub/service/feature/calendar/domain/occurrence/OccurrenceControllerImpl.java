package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.feature.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.api.feature.calendar.server.OccurrenceController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.CreateOccurrenceService;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.DeleteOccurrenceService;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.EditOccurrenceService;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.OccurrenceQueryService;
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
    public OneParamResponse<UUID> createOccurrence(OccurrenceRequest request, UUID eventId, AccessToken accessToken) {
        log.info("{} wants to create an Occurrence", accessToken.getUserId());
        log.debug("eventId: {}", eventId);
        log.debug(request.toString());

        UUID occurrenceId = createOccurrenceService.createOccurrence(accessToken.getUserId(), eventId, request);
        OneParamResponse<UUID> response = new OneParamResponse<>(occurrenceId);
        log.debug("Response: {}", response);

        return response;
    }

    @Override
    public void editOccurrence(OccurrenceRequest request, UUID occurrenceId, AccessToken accessToken) {
        log.info("{} wants to edit Occurrence {}", accessToken.getUserId(), occurrenceId);
        log.debug("occurrenceId: {}", occurrenceId);
        log.debug(request.toString());

        editOccurrenceService.editOccurrence(occurrenceId, request);

        log.debug("Response: {}", HttpStatus.OK);
    }

    @Override
    public void deleteOccurrence(UUID occurrenceId, AccessToken accessToken) {
        log.info("{} wants to delete Occurrence {}", accessToken.getUserId(), occurrenceId);

        deleteOccurrenceService.deleteOccurrence(occurrenceId);

        log.debug("Response: {}", HttpStatus.OK);
    }

    @Override
    public List<OccurrenceResponse> getOccurrences(LocalDate startDate, LocalDate endDate, UUID labelId, AccessToken accessToken) {
        log.info("{} wants to get Occurrences for label {}", accessToken.getUserId(), labelId);
        log.info("labelId: {}, startDate: {}, endDate: {}", labelId, startDate, endDate);

        List<OccurrenceResponse> occurrences = occurrenceQueryService.getOccurrences(accessToken.getUserId(), startDate, endDate, labelId);
        log.debug("Response: {}", occurrences);

        return occurrences;
    }

    @Override
    public OccurrenceResponse getOccurrence(UUID occurrenceId, AccessToken accessToken) {
        log.info("{} wants to get Occurrence {}", accessToken.getUserId(), occurrenceId);

        OccurrenceResponse occurrence = occurrenceQueryService.getOccurrence(occurrenceId);
        log.debug("Response: {}", occurrence);

        return occurrence;
    }

    @Override
    public List<OccurrenceResponse> getOccurrencesOfEvent(UUID eventId, AccessToken accessToken) {
        log.info("{} wants to get Occurrences of eventId {}", accessToken.getUserId(), eventId);

        List<OccurrenceResponse> response = occurrenceQueryService.getOccurrencesOfEvent(eventId);
        log.debug("Response: {}", response);

        return response;
    }

    @Override
    public OccurrenceResponse editOccurrenceStatus(OneParamRequest<OccurrenceStatus> status, UUID occurrenceId, AccessToken accessToken) {
        log.info("{} wants to edit status of Occurrence {}", accessToken.getUserId(), occurrenceId);
        log.debug(status.toString());

        ValidationUtil.notNull(status.getValue(), "status");

        OccurrenceResponse response = editOccurrenceService.editOccurrenceStatus(occurrenceId, status.getValue());
        log.debug("Response: {}", response);

        return response;
    }

    @Override
    public OccurrenceResponse setReminded(UUID occurrenceId, AccessToken accessToken) {
        log.info("{} wants to set reminded to true for Occurrence {}", accessToken.getUserId(), occurrenceId);

        OccurrenceResponse response = editOccurrenceService.setReminded(occurrenceId);
        log.debug("Response: {}", response);

        return response;
    }
}
