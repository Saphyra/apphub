package com.github.saphyra.apphub.service.diary.service.occurrence;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.EditOccurrenceRequest;
import com.github.saphyra.apphub.api.diary.server.OccurrenceController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.EditOccurrenceService;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.MarkOccurrenceDefaultService;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.MarkOccurrenceDoneService;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.MarkOccurrenceSnoozedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class OccurrenceControllerImpl implements OccurrenceController {
    private final EditOccurrenceService editOccurrenceService;
    private final MarkOccurrenceDoneService markOccurrenceDoneService;
    private final MarkOccurrenceSnoozedService markOccurrenceSnoozedService;
    private final MarkOccurrenceDefaultService markOccurrenceDefaultService;

    @Override
    public List<CalendarResponse> editOccurrence(EditOccurrenceRequest request, UUID occurrenceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit occurrence {}", accessTokenHeader.getUserId(), occurrenceId);
        log.debug("EditOccurrenceRequest: {}", request);

        return editOccurrenceService.edit(accessTokenHeader.getUserId(), occurrenceId, request);
    }

    @Override
    public CalendarResponse markOccurrenceDone(UUID occurrenceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to mark occurrence {} done", accessTokenHeader.getUserId(), occurrenceId);

        return markOccurrenceDoneService.markDone(accessTokenHeader.getUserId(), occurrenceId);
    }

    @Override
    public CalendarResponse markOccurrenceSnoozed(UUID occurrenceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to mark occurrence {} snoozed", accessTokenHeader.getUserId(), occurrenceId);

        return markOccurrenceSnoozedService.markSnoozed(accessTokenHeader.getUserId(), occurrenceId);
    }

    @Override
    public CalendarResponse markOccurrenceDefault(UUID occurrenceId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to mark occurrence {} default", accessTokenHeader.getUserId(), occurrenceId);

        return markOccurrenceDefaultService.markDefault(accessTokenHeader.getUserId(), occurrenceId);
    }
}
