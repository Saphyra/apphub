package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventDao {
    private final EventRepository repository;
    private final EventConverter converter;
    private final UuidConverter uuidConverter;
    private final EventCache eventCache;

    public Event findByIdValidated(UUID userId, UUID eventId) {
        return findById(userId, eventId)
            .orElseThrow(() -> ExceptionFactory.notFound("Event not found by id " + eventId));
    }

    public Optional<Event> findById(UUID userId, UUID eventId) {
        return Optional.ofNullable(getByUserId(userId).get(eventId));
    }

    public Map<UUID, Event> getByUserId(UUID userId) {
        Supplier<Map<UUID, Event>> loader = () -> converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)))
            .stream()
            .collect(Collectors.toMap(Event::getEventId, e -> e));
        return eventCache.get(userId, loader);
    }

    public void save(Event event) {
        repository.save(converter.convertDomain(event));
        eventCache.invalidate(event.getUserId());
    }

    public void delete(UUID userId, List<UUID> eventId) {
        repository.delete(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(eventId));
        eventCache.invalidate(userId);
    }

    /**
     * @param eventIds List<BiWrapper<userId, eventId>>
     */
    public List<Event> getByIds(List<BiWrapper<UUID, UUID>> eventIds) {
        return eventIds.stream()
            .map(bw -> findById(bw.getEntity1(), bw.getEntity2()))
            .flatMap(Optional::stream)
            .toList();
    }

    public void invalidate(UUID userId) {
        eventCache.invalidate(userId);
    }
}
