package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.common.Operation;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OccurrenceObjectQueryService {
    private final FindOccurrenceService findOccurrenceService;
    private final GetOccurrencesOfUserService getOccurrencesOfUserService;
    private final FindOccurrenceForOperationService findOccurrenceForOperationService;
    private final GetOccurrencesOfEventService getOccurrencesOfEventService;

    public BiWrapper<Event, Occurrence> findOccurrence(UUID userId, UUID eventId, UUID occurrenceId) {
        return findOccurrenceService.findOccurrence(userId, eventId, occurrenceId);
    }

    public Map<Event, List<Occurrence>> getOccurrences(UUID userId) {
        return getOccurrencesOfUserService.getOccurrences(userId);
    }

    public BiWrapper<Event, Occurrence> findOccurrence(UUID userId, UUID eventId, UUID occurrenceId, Operation operation) {
        return findOccurrenceForOperationService.findOccurrence(userId, eventId, occurrenceId, operation);
    }

    public BiWrapper<Event, List<Occurrence>> getOccurrences(UUID userId, UUID eventId) {
        return getOccurrencesOfEventService.getOccurrences(userId, eventId);
    }
}
