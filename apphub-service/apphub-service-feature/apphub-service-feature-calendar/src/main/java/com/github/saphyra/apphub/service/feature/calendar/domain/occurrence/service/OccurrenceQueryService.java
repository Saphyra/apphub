package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query.OccurrenceObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class OccurrenceQueryService {
    private final DateTimeUtil dateTimeUtil;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final OccurrenceResponseMapper occurrenceResponseMapper;
    private final OccurrenceQueryServiceHelper helper;
    private final OccurrenceObjectQueryService occurrenceObjectQueryService;

    public List<OccurrenceResponse> getOccurrences(UUID userId, LocalDate startDate, LocalDate endDate, UUID labelId) {
        Map<Event, List<Occurrence>> occurrenceMapping = occurrenceObjectQueryService.getOccurrences(userId);

        Map<UUID, Collection<UUID>> labels = getLabels(userId, occurrenceMapping.keySet());

        LocalDate currentDate = dateTimeUtil.getCurrentDate();
        List<Occurrence> occurrences = occurrenceMapping.entrySet()
            .stream()
            .filter(entry -> isNull(labelId) || labels.get(entry.getKey().getEventId()).contains(labelId)) //Filter for occurrences of events with given label
            .flatMap(entry -> getOccurrences(entry.getKey(), entry.getValue(), currentDate, startDate, endDate).stream())
            .toList();

        return occurrenceResponseMapper.toResponse(userId, occurrenceMapping.keySet(), occurrences);
    }

    private Map<UUID, Collection<UUID>> getLabels(UUID userId, Collection<Event> events) {
        List<UUID> eventIds = events.stream()
            .map(Event::getEventId)
            .toList();
        return eventLabelMappingDao.getLabelsOfEvents(userId, eventIds)
            .stream()
            .collect(Collectors.toMap(EventLabelMapping::getEventId, mapping -> mapping.getLabelIds().keySet()));
    }

    private List<Occurrence> getOccurrences(Event event, List<Occurrence> occurrences, LocalDate currentDate, LocalDate startDate, LocalDate endDate) {
        return occurrences.stream()
            .flatMap(occurrence -> helper.getOccurrencesToAdd(event, occurrence, currentDate, startDate, endDate).stream())
            .toList();
    }

    public List<OccurrenceResponse> getOccurrencesOfEvent(UUID userId, UUID eventId) {
        BiWrapper<Event, List<Occurrence>> occurrenceMapping = occurrenceObjectQueryService.getOccurrences(userId, eventId);

        return occurrenceResponseMapper.toResponse(userId, occurrenceMapping.getEntity1(), occurrenceMapping.getEntity2());
    }

    public OccurrenceResponse getOccurrence(UUID userId, UUID eventId, UUID occurrenceId) {
        BiWrapper<Event, Occurrence> occurrence = occurrenceObjectQueryService.findOccurrence(userId, eventId, occurrenceId)
            .orElseThrow(() -> ExceptionFactory.notFound("Occurrence not found for userId " + userId + ", eventId " + eventId + ", occurrenceId " + occurrenceId + " or user has no access to it."));
        return occurrenceResponseMapper.toResponse(userId, occurrence.getEntity1(), occurrence.getEntity2());
    }
}
