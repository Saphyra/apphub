package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.service.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.calendar.domain.event.dao.EventDao;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
class EventCache {
    private final Map<UUID, Event> cache = new HashMap<>();
    @NonNull
    private final EventDao eventDao;

    public Event get(UUID eventId) {
        return cache.computeIfAbsent(eventId, eventDao::findByIdValidated);
    }
}
