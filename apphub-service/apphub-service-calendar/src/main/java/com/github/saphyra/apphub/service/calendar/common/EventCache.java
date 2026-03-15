package com.github.saphyra.apphub.service.calendar.common;

import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@RequiredArgsConstructor
public class EventCache {
    private final Map<UUID, Event> cache = new ConcurrentHashMap<>();
    @NonNull
    private final EventDao eventDao;

    public Event get(UUID eventId) {
        return cache.computeIfAbsent(eventId, eventDao::findByIdValidated);
    }

    public void load(Function<EventDao, List<Event>> loader) {
        loader.apply(eventDao)
            .forEach(event -> cache.put(event.getEventId(), event));
    }

    public Collection<Event> getAll() {
        return cache.values();
    }
}
