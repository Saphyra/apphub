package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.api.feature.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceFactory;
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
    private final OccurrenceRequestValidator occurrenceRequestValidator;
    private final EventDao eventDao;
    private final OccurrenceFactory occurrenceFactory;
    private final OccurrenceDao occurrenceDao;

    /**
     * Creates occurrences for the newly created event.
     */
    public List<Occurrence> createOccurrences(UUID userId, UUID eventId, EventRequest request) {
        return occurrenceCreators.stream()
            .filter(occurrenceCreator -> occurrenceCreator.getRepetitionType() == request.getRepetitionType())
            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException("Repetition type not supported: " + request.getRepetitionType()))
            .createOccurrences(userId, eventId, request);
    }

    public UUID createOccurrence(UUID userId, UUID eventId, OccurrenceRequest request) {
        occurrenceRequestValidator.validate(request);

        eventDao.findByIdValidated(userId, eventId); // Ensure the event exists and belongs to the user

        Occurrence occurrence = occurrenceFactory.create(eventId, request.getDate(), request.getTime(), request.getRemindMeBeforeDays(), request.getNote());
        occurrenceDao.save(userId, occurrence);

        return occurrence.getOccurrenceId();
    }
}
