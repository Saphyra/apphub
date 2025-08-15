package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.response.OccurrenceResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class OccurrenceQueryService {
    private final OccurrenceDao occurrenceDao;
    private final DateTimeUtil dateTimeUtil;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final OccurrenceMapper occurrenceMapper;
    private final EventDao eventDao;

    public List<OccurrenceResponse> getOccurrences(UUID userId, LocalDate startDate, LocalDate endDate, UUID labelId) {
        LazyLoadedField<List<UUID>> eventsOfLabel = new LazyLoadedField<>(() -> getEventsOfLabel(userId, labelId));
        EventCache eventCache = new EventCache(eventDao);

        return occurrenceDao.getByUserId(userId)
            .stream()
            .filter(occurrence -> dateTimeUtil.isBetween(occurrence.getDate(), startDate, endDate)) //TODO include reminders of occurrences out of range
            .filter(occurrence -> isNull(labelId) || eventsOfLabel.get().contains(occurrence.getEventId()))
            .map(occurrence -> occurrenceMapper.toResponse(eventCache, occurrence))
            .toList();
    }

    private List<UUID> getEventsOfLabel(UUID userId, UUID labelId) {
        return eventLabelMappingDao.getByUserIdAndLabelId(userId, labelId)
            .stream()
            .map(EventLabelMapping::getEventId)
            .collect(Collectors.toList());
    }

    public List<OccurrenceResponse> getOccurrencesOfEvent(UUID eventId) {
        EventCache eventCache = new EventCache(eventDao);

        return occurrenceDao.getByEventId(eventId)
            .stream()
            .map(occurrence -> occurrenceMapper.toResponse(eventCache, occurrence))
            .toList();
    }

    public OccurrenceResponse getOccurrence(UUID occurrenceId) {
        Occurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);
        return occurrenceMapper.toResponse(occurrence);
    }
}
