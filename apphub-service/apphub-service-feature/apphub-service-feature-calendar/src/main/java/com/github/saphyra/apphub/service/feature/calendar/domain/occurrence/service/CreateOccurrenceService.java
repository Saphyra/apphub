package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.api.feature.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao.DeprecatedOccurrenceFactory;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
public class CreateOccurrenceService {
    private final List<OccurrenceCreator> occurrenceCreators;
    private final DeprecatedOccurrenceDao occurrenceDao;
    private final DeprecatedOccurrenceFactory occurrenceFactory;
    private final OccurrenceRequestValidator occurrenceRequestValidator;
    private final DeprecatedEventDao eventDao;

    /**
     * Creates occurrences for the newly created event.
     */
    public void createOccurrences(UUID userId, UUID eventId, EventRequest request) {
        occurrenceCreators.stream()
            .filter(occurrenceCreator -> occurrenceCreator.getRepetitionType() == request.getRepetitionType())
            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException("Repetition type not supported: " + request.getRepetitionType()))
            .createOccurrences(userId, eventId, request);
    }

    public UUID createOccurrence(UUID userId, UUID eventId, OccurrenceRequest request) {
        occurrenceRequestValidator.validate(request);

        eventDao.findByIdValidated(eventId); // Ensure the event exists and belongs to the user

        DeprecatedOccurrence occurrence = occurrenceFactory.create(userId, eventId, request.getDate(), request.getTime(), request.getRemindMeBeforeDays(), request.getNote());
        occurrenceDao.save(occurrence);

        return occurrence.getOccurrenceId();
    }
}
