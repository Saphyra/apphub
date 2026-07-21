package com.github.saphyra.apphub.service.feature.calendar.domain.event.service;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.api.feature.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EventQueryService {
    private final EventDao eventDao;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final EventResponseMapper eventResponseMapper;
    private final AlmDao almDao;

    public List<EventResponse> getEvents(UUID userId, UUID labelId) {
        List<BiWrapper<Event, Boolean>> events;
        if (isNull(labelId)) {
            events = getAllEvents(userId);
        } else {
            events = almDao.findForObject(userId, PrincipalType.USER, labelId, SharedObjectType.LABEL)
                .map(Alm::getOwner)
                .or(() -> Optional.of(userId))
                .map(ownerId -> eventLabelMappingDao.getEventsOfLabel(ownerId, labelId))
                .orElseThrow()
                .getEventIds()
                .entrySet()
                .stream()
                .map(entry -> new BiWrapper<>(eventDao.findByIdValidated(entry.getValue(), entry.getKey()), !entry.getValue().equals(userId)))
                .toList();
        }

        return eventResponseMapper.toResponse(userId, events);
    }

    private List<BiWrapper<Event, Boolean>> getAllEvents(UUID userId) {
        List<BiWrapper<Event, Boolean>> ownEvents = eventDao.getByUserId(userId)
            .stream()
            .map(event -> new BiWrapper<>(event, false))
            .toList();

        List<BiWrapper<Event, Boolean>> sharedEvents = almDao.getByUserIdAndObjectType(userId, SharedObjectType.EVENT)
            .stream()
            .map(alm -> new BiWrapper<>(eventDao.findByIdValidated(alm.getOwner(), alm.getObjectId()), true))
            .toList();

        List<BiWrapper<Event, Boolean>> sharedByLabel = almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            .flatMap(this::queryEventsBySharedLabel)
            .map(event -> new BiWrapper<>(event, true))
            .toList();

        return Stream.of(ownEvents, sharedEvents, sharedByLabel)
            .flatMap(Collection::stream)
            .distinct()
            .toList();
    }

    private Stream<Event> queryEventsBySharedLabel(Alm alm) {
        return eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId())
            .getEventIds()
            .entrySet()
            .stream()
            .map(entry -> eventDao.findByIdValidated(entry.getValue(), entry.getKey()));
    }

    public List<EventResponse> getLabellessEvents(UUID userId) {
        List<UUID> eventIds = eventLabelMappingDao.getLabelsOfEventsByUserId(userId)
            .stream()
            .filter(mapping -> mapping.getLabelIds().isEmpty())
            .map(EventLabelMapping::getEventId)
            .toList();

        return eventDao.getByIds(userId, eventIds)
            .stream()
            .map(event -> eventResponseMapper.toResponse(event, false, List.of()))
            .toList();
    }

    public EventResponse getEvent(UUID userId, UUID eventId) {
        return eventResponseMapper.toResponse(userId, eventDao.findByIdValidated(userId, eventId), false);
    }
}
