package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class EventDao {
    public Event findByIdValidated(UUID userId, UUID eventId) {
        return null;
    }

    public List<Event> getByUserId(UUID userId) {
        return null;
    }

    public List<Event> getByIds(UUID userId, Collection<UUID> eventIds) {
        return null;
    }

    public void save(Event event) {

    }
}
