package com.github.saphyra.apphub.service.calendar.domain.event.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EventDao extends AbstractDao<EventEntity, Event, String, EventRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    EventDao(EventConverter converter, EventRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<Event> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public void deleteByUserIdAndEventId(UUID userId, UUID eventId) {
        repository.deleteByUserIdAndEventId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(eventId));
    }

    public Event findByIdValidated(UUID eventId) {
        return findById(eventId)
            .orElseThrow(() -> ExceptionFactory.notFound("Event not found by eventId " + eventId));
    }

    private Optional<Event> findById(UUID eventId) {
        return findById(uuidConverter.convertDomain(eventId));
    }
}
