package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.request.EventRequest;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;

import java.util.List;
import java.util.UUID;

public interface OccurrenceCreator extends OccurrenceRepetitionTypeAware {
    List<Occurrence> createOccurrences(UUID userId, UUID eventId, EventRequest request);
}
