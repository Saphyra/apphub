package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class DeleteEventService {
    private final EventDao eventDao;
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final CommonCalendarDao commonCalendarDao;

    public void delete(UUID userId, UUID eventId) {
        eventDao.findById(userId, eventId)
            .or(() -> almDao.findForObject(userId, PrincipalType.USER, eventId, SharedObjectType.EVENT).flatMap(alm -> eventDao.findById(alm.getOwner(), alm.getObjectId())))
            .or(() -> findEventOfSharedLabel(userId, eventId))
            .ifPresent(event -> delete(event.getUserId(), List.of(event.getEventId())));

        delete(userId, List.of(eventId));
    }

    private Optional<Event> findEventOfSharedLabel(UUID userId, UUID eventId) {
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            .map(alm -> eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId()))
            .flatMap(mapping -> mapping.getEventIds().entrySet().stream())
            .filter(entry -> entry.getKey().equals(eventId))
            .findFirst()
            .flatMap(entry -> eventDao.findById(entry.getValue(), entry.getKey()));
    }

    public void delete(UUID userId, List<UUID> deletedEventIds) {
        commonCalendarDao.deleteEvents(userId, deletedEventIds);
    }
}
