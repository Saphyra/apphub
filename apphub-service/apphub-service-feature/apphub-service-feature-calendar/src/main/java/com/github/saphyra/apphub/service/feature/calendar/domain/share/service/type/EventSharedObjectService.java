package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query.EventObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class EventSharedObjectService implements SharedObjectService {
    private final EventObjectQueryService eventObjectQueryService;
    private final EventDao eventDao;

    @Override
    public SharedObjectType getType() {
        return SharedObjectType.EVENT;
    }

    @Override
    public SharedObject getSharedObject(UUID userId, UUID eventId, UUID parent) {
        Event event = eventObjectQueryService.findEvent(userId, eventId)
            .orElseThrow(() -> ExceptionFactory.notFound("Event not found for owner %s and eventId %s".formatted(userId, eventId)));

        return new SharedObject(event.getEventId(), event.getUserId(), event.getUserId(), event.getTitle());
    }

    @Override
    public boolean exists(UUID userId, UUID eventId) {
        return eventDao.findById(userId, eventId)
            .isPresent();
    }
}
