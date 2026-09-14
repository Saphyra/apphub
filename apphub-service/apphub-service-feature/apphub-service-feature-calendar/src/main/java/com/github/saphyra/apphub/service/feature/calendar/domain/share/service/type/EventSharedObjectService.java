package com.github.saphyra.apphub.service.feature.calendar.domain.share.service.type;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query.EventObjectQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
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
    public Optional<SharedObject> getSharedObject(UUID principal, UUID eventId, UUID parent) {
        return eventObjectQueryService.findEvent(principal, eventId)
            .map(event -> new SharedObject(event.getEventId(), event.getUserId(), event.getUserId(), event.isMasked() ? Constants.QUESTION_MARK : event.getTitle()));
    }

    @Override
    public boolean exists(UUID userId, UUID eventId) {
        return eventDao.findById(userId, eventId)
            .isPresent();
    }
}
