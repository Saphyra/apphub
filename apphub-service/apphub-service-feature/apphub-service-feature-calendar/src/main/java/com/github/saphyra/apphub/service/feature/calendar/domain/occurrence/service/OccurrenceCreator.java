package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;

import java.util.UUID;

public interface OccurrenceCreator extends OccurrenceRepetitionTypeAware {
    void createOccurrences(UUID userId, UUID eventId, EventRequest request);
}
