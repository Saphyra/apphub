package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
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
    private final OccurrenceDao occurrenceDao;
    private final DateTimeUtil dateTimeUtil;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final OccurrenceResponseMapper occurrenceResponseMapper;
    private final EventDao eventDao;
    private final OccurrenceQueryServiceHelper helper;

    public List<OccurrenceResponse> getOccurrences(UUID userId, LocalDate startDate, LocalDate endDate, UUID labelId) {
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

        return occurrenceResponseMapper.toResponse(events, occurrences);
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
        Occurrence occurrence = occurrenceDao.findByIdValidated(eventId, occurrenceId);
        return occurrenceResponseMapper.toResponse(userId, occurrence);
    }
}
