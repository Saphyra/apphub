package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EventSharedObjectService implements SharedObjectService {
    private final EventDao eventDao;

    @Override
    public SharedObjectType getType() {
        return SharedObjectType.EVENT;
    }

    @Override
    public SharedObject getSharedObject(UUID owner, UUID objectId, UUID parent) {
        Event event = eventDao.findByIdValidated(owner, objectId);

        return new SharedObject(event.getEventId(), event.getUserId(), event.getUserId(), event.getTitle());
    }
}
