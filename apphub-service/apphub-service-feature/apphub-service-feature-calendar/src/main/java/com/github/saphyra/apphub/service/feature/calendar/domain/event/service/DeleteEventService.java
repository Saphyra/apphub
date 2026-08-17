package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query.EventObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.common.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteEventService {
    private final CommonCalendarDao commonCalendarDao;
    private final EventObjectQueryService eventObjectQueryService;

    public void delete(UUID userId, UUID eventId) {
        eventObjectQueryService.findEvent(userId, eventId, Operation.DELETE)
            .ifPresent(event -> delete(event.getUserId(), List.of(event.getEventId())));
    }

    public void delete(UUID userId, List<UUID> deletedEventIds) {
        commonCalendarDao.deleteEvents(userId, deletedEventIds);
    }
}
