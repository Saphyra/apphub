package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
class OccurrenceSharedObjectService implements SharedObjectService {
    private final OccurrenceDao occurrenceDao;

    @Override
    public SharedObjectType getType() {
        return SharedObjectType.OCCURRENCE;
    }

    @Override
    public SharedObject getSharedObject(UUID owner, UUID objectId, UUID eventId) {
        Occurrence occurrence = occurrenceDao.findByIdValidated(eventId, objectId);

        return new SharedObject(occurrence.getOccurrenceId(), occurrence.getUserId(), occurrence.getEventId(), occurrence.getDate().toString());
    }
}
