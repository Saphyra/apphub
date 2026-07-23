package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class OccurrenceResponseMapper {
    private final EventDao eventDao;
    private final OccurrenceDao occurrenceDao;
    private final AlmDao almDao;
    private final EventLabelMappingDao eventLabelMappingDao;

    public List<OccurrenceResponse> toResponse(UUID userId, List<Occurrence> occurrences) {
        Map<UUID, Event> events = occurrences.stream()
            .map(Occurrence::getEventId)
            .distinct()
            .map(eventId -> queryEvent(userId, eventId))
            .collect(Collectors.toMap(Event::getEventId, event -> event));

        return toResponse(userId, events, occurrences);
    }

    private Event queryEvent(UUID userId, UUID eventId) {
        return eventDao.findById(userId, eventId)
            .or(() -> almDao.findForObject(userId, PrincipalType.USER, eventId, SharedObjectType.EVENT).flatMap(alm -> eventDao.findById(alm.getOwner(), eventId)))
            .or(() -> findEventBySharedLabel(userId, eventId))
            .orElseThrow(() -> ExceptionFactory.notFound(userId + " has no access to event " + eventId + " or event does not exist."));
    }

    //TODO verify access rights
    private Optional<Event> findEventBySharedLabel(UUID userId, UUID eventId) {
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            .map(alm -> eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId()))
            .filter(mapping -> mapping.getEventIds().containsKey(eventId))
            .findAny()
            .map(mapping -> eventDao.findByIdValidated(mapping.getEventIds().get(eventId), eventId));
    }

    public List<OccurrenceResponse> toResponse(UUID userId, Map<UUID, Event> events, List<Occurrence> occurrences) {
        return occurrences.stream()
            .map(occurrence -> toResponse(userId, events.get(occurrence.getEventId()), occurrence))
            .toList();
    }

    public OccurrenceResponse toResponse(UUID userId, Occurrence occurrence) {
        Event event = eventDao.findByIdValidated(userId, occurrence.getEventId());

        return toResponse(userId, event, occurrence);
    }

    private OccurrenceResponse toResponse(UUID userId, Event event, Occurrence occurrence) {
        Boolean autoDone = getFromEventIfNull(event, occurrence.getAutoDone(), Event::isAutoDone);
        if (occurrence.getStatus() == OccurrenceStatus.EXPIRED && autoDone) {
            occurrence.setStatus(OccurrenceStatus.DONE);
            occurrenceDao.save(occurrence);
        }

        return OccurrenceResponse.builder()
            .occurrenceId(occurrence.getOccurrenceId())
            .eventId(occurrence.getEventId())
            .date(occurrence.getDate())
            .time(getFromEventIfNull(event, occurrence.getTime(), Event::getTime))
            .status(occurrence.getStatus())
            .title(event.getTitle())
            .content(event.getContent())
            .note(occurrence.getNote())
            .remindMeBeforeDays(getFromEventIfNull(event, occurrence.getRemindMeBeforeDays(), Event::getRemindMeBeforeDays))
            .reminded(occurrence.isReminded())
            .eventArchived(event.isArchived())
            .autoDone(autoDone)
            .shared(!userId.equals(occurrence.getUserId()))
            .build();
    }

    private <T> T getFromEventIfNull(Event event, T value, Function<Event, T> mapper) {
        return Optional.ofNullable(value)
            .orElseGet(() -> mapper.apply(event));
    }
}
