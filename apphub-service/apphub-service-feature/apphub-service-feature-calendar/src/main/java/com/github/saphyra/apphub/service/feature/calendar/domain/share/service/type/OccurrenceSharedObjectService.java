package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query.OccurrenceObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class OccurrenceSharedObjectService implements SharedObjectService {
    private final OccurrenceObjectQueryService occurrenceObjectQueryService;
    private final DateTimeConverter dateTimeConverter;
    private final OccurrenceDao occurrenceDao;

    @Override
    public SharedObjectType getType() {
        return SharedObjectType.OCCURRENCE;
    }

    @Override
    public SharedObject getSharedObject(UUID userId, UUID occurrenceId, UUID eventId) {
        Occurrence occurrence = occurrenceObjectQueryService.findOccurrence(userId, eventId, occurrenceId)
            .getEntity2();

        return new SharedObject(occurrence.getOccurrenceId(), occurrence.getUserId(), occurrence.getEventId(), dateTimeConverter.convertDomain(occurrence.getDate()));
    }

    @Override
    public boolean exists(UUID eventId, UUID occurrenceId) {
        return occurrenceDao.findById(eventId, occurrenceId)
            .isPresent();
    }
}
