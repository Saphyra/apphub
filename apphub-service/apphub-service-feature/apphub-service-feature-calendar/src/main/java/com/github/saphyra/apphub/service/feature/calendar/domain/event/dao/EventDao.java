package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class EventDao {
    private final EventRepository repository;
    private final EventConverter converter;
    private final UuidConverter uuidConverter;

    public Event findByIdValidated(UUID userId, UUID eventId) {
        return converter.convertEntity(repository.findById(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(eventId)))
            .orElseThrow(() -> ExceptionFactory.notFound("Event not found by id " + eventId));
    }

    public List<Event> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public List<Event> getByIds(UUID userId, Collection<UUID> eventIds) {
        String userIdString = uuidConverter.convertDomain(userId);

        return Lists.partition(new ArrayList<>(eventIds), Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE)
            .stream()
            .map(batch -> batch.stream().map(eventId -> new BiWrapper<>(userIdString, uuidConverter.convertDomain(eventId))).toList())
            .flatMap(batch -> repository.getByIds(batch).stream())
            .map(converter::convertEntity)
            .toList();
    }

    public void save(Event event) {
        repository.save(converter.convertDomain(event));
    }

    public void delete(UUID userId, List<UUID> eventId) {
        String userIdString = uuidConverter.convertDomain(userId);

        Lists.partition(eventId, Constants.DYNAMO_DB_QUERY_MAX_BATCH_SIZE)
            .forEach(batch -> repository.delete(userIdString, batch.stream().map(uuidConverter::convertDomain).toList()));
    }
}
