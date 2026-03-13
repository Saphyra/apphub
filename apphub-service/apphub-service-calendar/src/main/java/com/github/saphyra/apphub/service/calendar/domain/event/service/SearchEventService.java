package com.github.saphyra.apphub.service.calendar.domain.event.service;

import com.github.saphyra.apphub.api.calendar.model.response.EventResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.calendar.common.EventCache;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchEventService {
    private final EventDao eventDao;
    private final OccurrenceDao occurrenceDao;
    private final EventMapper eventMapper;

    public List<EventResponse> search(UUID userId, String search) {
        ValidationUtil.minLength(search, 3, "searchText");

        EventCache eventCache = new EventCache(eventDao);
        eventCache.load(dao -> dao.getByUserId(userId));

        return Stream.concat(
                getMatchingEvents(eventCache, search),
                getMatchingOccurrences(userId, eventCache, search)
            )
            .distinct()
            .map(eventMapper::toResponse)
            .toList();
    }

    private Stream<Event> getMatchingEvents(EventCache eventCache, String search) {
        return eventCache.getAll()
            .stream()
            .filter(event -> matches(search, event.getTitle()) || matches(search, event.getContent()));
    }

    private Stream<Event> getMatchingOccurrences(UUID userId, EventCache eventCache, String search) {
        return occurrenceDao.getByUserId(userId)
            .stream()
            .filter(occurrence -> matches(search, occurrence.getNote()))
            .map(occurrence -> eventCache.get(occurrence.getEventId()));
    }

    private static boolean matches(String search, String value) {
        return value.toLowerCase().contains(search.toLowerCase());
    }
}
