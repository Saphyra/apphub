package com.github.saphyra.apphub.service.feature.calendar.common;

import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEvent;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao.DeprecatedEventDao;
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
    private final Map<UUID, DeprecatedEvent> cache = new ConcurrentHashMap<>();
    @NonNull
    private final DeprecatedEventDao eventDao;

    public DeprecatedEvent get(UUID eventId) {
        return cache.computeIfAbsent(eventId, eventDao::findByIdValidated);
    }

    public void load(Function<DeprecatedEventDao, List<DeprecatedEvent>> loader) {
        loader.apply(eventDao)
            .forEach(event -> cache.put(event.getEventId(), event));
    }

    public Collection<DeprecatedEvent> getAll() {
        return cache.values();
    }
}
