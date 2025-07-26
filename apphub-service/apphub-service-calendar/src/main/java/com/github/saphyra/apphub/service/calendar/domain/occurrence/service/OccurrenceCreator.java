package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.api.calendar.model.request.EventRequest;

import java.util.UUID;

public interface OccurrenceCreator {
    RepetitionType getRepetitionType();

    void createOccurrences(UUID userId, UUID eventId, EventRequest request);
}
