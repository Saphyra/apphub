package com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Deprecated(forRemoval = true)
public class DeprecatedEventDao extends AbstractDao<DeprecatedEventEntity, DeprecatedEvent, String, DeprecatedEventRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    DeprecatedEventDao(DeprecatedEventConverter converter, DeprecatedEventRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<DeprecatedEvent> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public void deleteByUserIdAndEventId(UUID userId, UUID eventId) {
        repository.deleteByUserIdAndEventId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(eventId));
    }

    public DeprecatedEvent findByIdValidated(UUID eventId) {
        return findById(eventId)
            .orElseThrow(() -> ExceptionFactory.notFound("DeprecatedEvent not found by eventId " + eventId));
    }

    private Optional<DeprecatedEvent> findById(UUID eventId) {
        return findById(uuidConverter.convertDomain(eventId));
    }
}
