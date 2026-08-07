package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventDao {
    private final EventRepository repository;
    private final EventConverter converter;
    private final UuidConverter uuidConverter;

    public Event findByIdValidated(UUID userId, UUID eventId) {
        return findById(userId, eventId)
            .orElseThrow(() -> ExceptionFactory.notFound("Event not found by id " + eventId));
    }

    public Optional<Event> findById(UUID userId, UUID eventId) {
        return converter.convertEntity(repository.findById(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(eventId)));
    }

    public List<Event> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public List<Event> getByIds(UUID userId, Collection<UUID> eventIds) {
        String userIdString = uuidConverter.convertDomain(userId);

        List<BiWrapper<String, String>> ids = eventIds.stream()
            .map(eventId -> new BiWrapper<>(userIdString, uuidConverter.convertDomain(eventId)))
            .toList();
        return converter.convertEntity(repository.getByIds(ids));
    }

    public void save(Event event) {
        repository.save(converter.convertDomain(event));
    }

    public void delete(UUID userId, List<UUID> eventId) {
        repository.delete(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(eventId));
    }

    /**
     * @param eventIds List<BiWrapper<userId, eventId>>
     */
    //TODO unit test
    public List<Event> getByIds(List<BiWrapper<UUID, UUID>> eventIds) {
        List<BiWrapper<String, String>> ids = eventIds.stream()
            .map(entry -> new BiWrapper<>(uuidConverter.convertDomain(entry.getEntity1()), uuidConverter.convertDomain(entry.getEntity2())))
            .toList();

        return converter.convertEntity(repository.getByIds(ids));
    }
}
