package com.github.saphyra.apphub.service.calendar_deprecated.service.calendar.search;

import com.github.saphyra.apphub.api.calendar.model.EventSearchResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.event.Event;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.event.EventDao;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class CalendarSearchService {
    private final EventDao eventDao;
    private final OccurrenceDao occurrenceDao;
    private final CalendarSearchResultMapper calendarSearchResultMapper;

    public List<EventSearchResponse> search(UUID userId, String query) {
        ValidationUtil.minLength(query, 3, "value");

        Map<UUID, Event> events = eventDao.getByUserId(userId)
            .stream()
            .collect(Collectors.toMap(Event::getEventId, Function.identity()));
        List<Occurrence> occurrences = occurrenceDao.getByUserId(userId);

        Set<UUID> eventIds = Stream.concat(
            searchEvents(events.values(), query),
            searchOccurrences(occurrences, query)
        ).collect(Collectors.toSet());

        return calendarSearchResultMapper.map(eventIds, events, occurrences);
    }

    private Stream<UUID> searchEvents(Collection<Event> values, String query) {
        return values.stream()
            .filter(event -> event.toString().toLowerCase().contains(query.toLowerCase()))
            .map(Event::getEventId);
    }

    private Stream<UUID> searchOccurrences(Collection<Occurrence> values, String query) {
        return values.stream()
            .filter(event -> event.toString().toLowerCase().contains(query.toLowerCase()))
            .map(Occurrence::getEventId);
    }
}
