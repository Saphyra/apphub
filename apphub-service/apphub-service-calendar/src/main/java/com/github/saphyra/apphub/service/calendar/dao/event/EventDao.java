package com.github.saphyra.apphub.service.calendar.dao.event;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class EventDao extends AbstractDao<EventEntity, Event, String, EventRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public EventDao(EventConverter converter, EventRepository repository, UuidConverter uuidConverter) {
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

    public Event findByIdValidated(UUID eventId) {
        return converter.convertEntity(repository.findById(uuidConverter.convertDomain(eventId)))
            .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Event not found with id " + eventId));
    }

    public void deleteById(UUID eventId) {
        deleteById(uuidConverter.convertDomain(eventId));
    }
}
