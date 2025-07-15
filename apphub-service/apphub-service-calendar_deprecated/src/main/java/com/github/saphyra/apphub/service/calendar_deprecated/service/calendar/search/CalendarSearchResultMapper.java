package com.github.saphyra.apphub.service.calendar_deprecated.service.calendar.search;

import com.github.saphyra.apphub.api.calendar.model.EventSearchResponse;
import com.github.saphyra.apphub.api.calendar.model.OccurrenceSearchResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.event.Event;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class CalendarSearchResultMapper {
    private final DateTimeUtil dateTimeUtil;

    public List<EventSearchResponse> map(Set<UUID> eventIds, Map<UUID, Event> events, List<Occurrence> occurrences) {
        return eventIds.stream()
            .map(eventId -> mapEvent(events.get(eventId), occurrences))
            .collect(Collectors.toList());
    }

    private EventSearchResponse mapEvent(Event event, List<Occurrence> occurrences) {
        return EventSearchResponse.builder()
            .eventId(event.getEventId())
            .time(dateTimeUtil.format(LocalDateTime.of(event.getStartDate(), Optional.ofNullable(event.getTime()).orElseGet(dateTimeUtil::getCurrentTime)), false))
            .repetitionType(event.getRepetitionType())
            .repetitionData(event.getRepetitionData())
            .title(event.getTitle())
            .content(event.getContent())
            .occurrences(mapOccurrences(event.getEventId(), occurrences))
            .build();
    }

    private List<OccurrenceSearchResponse> mapOccurrences(UUID eventId, List<Occurrence> occurrences) {
        return occurrences.stream()
            .filter(occurrence -> occurrence.getEventId().equals(eventId))
            .filter(occurrence -> occurrence.getStatus() != OccurrenceStatus.VIRTUAL)
            .map(occurrence -> OccurrenceSearchResponse.builder()
                .occurrenceId(occurrence.getOccurrenceId())
                .date(occurrence.getDate())
                .time(occurrence.getTime())
                .status(occurrence.getStatus().name())
                .note(occurrence.getNote())
                .build())
            .collect(Collectors.toList());
    }
}
