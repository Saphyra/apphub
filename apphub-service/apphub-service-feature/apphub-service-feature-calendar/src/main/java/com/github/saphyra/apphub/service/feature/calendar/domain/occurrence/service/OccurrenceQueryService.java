package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO move to helper
public class OccurrenceQueryService {
    private final OccurrenceDao occurrenceDao;
    private final DateTimeUtil dateTimeUtil;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final OccurrenceResponseMapper occurrenceResponseMapper;
    private final EventDao eventDao;
    private final OccurrenceQueryServiceHelper helper;
    private final AlmDao almDao;

    public List<OccurrenceResponse> getOccurrences(UUID userId, LocalDate startDate, LocalDate endDate, UUID labelId) {
        Map<UUID, List<Occurrence>> occurrenceMapping = Stream.of(
                getOwnOccurrences(userId),
                getSharedOccurrences(userId),
                getSharedEventOccurrences(userId),
                getSharedLabelOccurrences(userId)
            )
            .flatMap(s -> s)
            .distinct()
            .collect(Collectors.groupingBy(Occurrence::getEventId));
        Map<UUID, Event> events = occurrenceMapping.keySet()
            .stream()
            .map(eventId -> findEvent(userId, eventId))
            .collect(Collectors.toMap(Event::getEventId, event -> event));
        Map<UUID, Collection<UUID>> labels = eventLabelMappingDao.getLabelsOfEvents(userId, events.keySet())
            .stream()
            .collect(Collectors.toMap(EventLabelMapping::getEventId, mapping -> mapping.getLabelIds().keySet()));

        LocalDate currentDate = dateTimeUtil.getCurrentDate();
        List<Occurrence> occurrences = occurrenceMapping.entrySet()
            .stream()
            .filter(entry -> isNull(labelId) || labels.get(entry.getKey()).contains(labelId)) //Filter for occurrences of events with given label
            .flatMap(entry -> getOccurrences(events.get(entry.getKey()), entry.getValue(), currentDate, startDate, endDate).stream())
            .toList();

        return occurrenceResponseMapper.toResponse(userId, events, occurrences);
    }

    //TODO verify access
    private Stream<Occurrence> getSharedLabelOccurrences(UUID userId) {
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.LABEL)
            .stream()
            .map(alm -> eventLabelMappingDao.getEventsOfLabel(alm.getOwner(), alm.getObjectId()).getEventIds())
            .flatMap(eventIds -> eventIds.keySet().stream())
            .distinct()
            .flatMap(eventId -> occurrenceDao.getByEventId(eventId).stream());
    }

    //TODO verify access
    private Stream<Occurrence> getSharedEventOccurrences(UUID userId) {
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.EVENT)
            .stream()
            .map(alm -> occurrenceDao.getByEventId(alm.getObjectId()))
            .flatMap(Collection::stream);
    }

    //TODO verify access
    private Stream<Occurrence> getSharedOccurrences(UUID userId) {
        return almDao.getByUserIdAndObjectType(userId, SharedObjectType.OCCURRENCE)
            .stream()
            .map(alm -> occurrenceDao.findByIdValidated(alm.getParent(), alm.getObjectId()));
    }

    private Stream<Occurrence> getOwnOccurrences(UUID userId) {
        return eventDao.getByUserId(userId)
            .stream()
            .flatMap(event -> occurrenceDao.getByEventId(event.getEventId()).stream());
    }

    //TODO verify access rights
    private Event findEvent(UUID userId, UUID eventId) {
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

    //TODO remove
    public List<OccurrenceResponse> getOccurrencesOld(UUID userId, LocalDate startDate, LocalDate endDate, UUID labelId) {
        LocalDate currentDate = dateTimeUtil.getCurrentDate();

        Map<UUID, List<Occurrence>> occurrenceMapping = helper.getOccurrencesBetween(userId, startDate, endDate)
            .stream()
            .collect(Collectors.groupingBy(Occurrence::getEventId));
        Map<UUID, Event> events = eventDao.getByIds(userId, occurrenceMapping.keySet())
            .stream()
            .collect(Collectors.toMap(Event::getEventId, event -> event));
        Map<UUID, Collection<UUID>> labels = eventLabelMappingDao.getLabelsOfEvents(userId, events.keySet())
            .stream()
            .collect(Collectors.toMap(EventLabelMapping::getEventId, mapping -> mapping.getLabelIds().keySet()));

        List<Occurrence> occurrences = occurrenceMapping.entrySet()
            .stream()
            .filter(entry -> isNull(labelId) || labels.get(entry.getKey()).contains(labelId)) //Filter for occurrences of events with given label
            .flatMap(entry -> getOccurrences(events.get(entry.getKey()), entry.getValue(), currentDate, startDate, endDate).stream())
            .toList();

        return occurrenceResponseMapper.toResponse(userId, events, occurrences);
    }

    private List<Occurrence> getOccurrences(Event event, List<Occurrence> occurrences, LocalDate currentDate, LocalDate startDate, LocalDate endDate) {
        return occurrences.stream()
            .flatMap(occurrence -> helper.getOccurrencesToAdd(event, occurrence, currentDate, startDate, endDate).stream())
            .toList();
    }

    public List<OccurrenceResponse> getOccurrencesOfEvent(UUID userId, UUID eventId) {
        List<Occurrence> occurrences = occurrenceDao.getByEventId(eventId);

        return occurrenceResponseMapper.toResponse(userId, occurrences);
    }

    public OccurrenceResponse getOccurrence(UUID userId, UUID eventId, UUID occurrenceId) {
        //TODO check if user has access to occurrence
        Occurrence occurrence = occurrenceDao.findByIdValidated(eventId, occurrenceId);
        return occurrenceResponseMapper.toResponse(userId, occurrence);
    }
}
