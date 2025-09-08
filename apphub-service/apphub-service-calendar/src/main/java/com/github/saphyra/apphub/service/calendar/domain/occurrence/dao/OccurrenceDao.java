package com.github.saphyra.apphub.service.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
//TODO unit test
public class OccurrenceDao extends AbstractDao<OccurrenceEntity, Occurrence, String, OccurrenceRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    OccurrenceDao(OccurrenceConverter converter, OccurrenceRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public void deleteByUserIdAndEventId(UUID userId, UUID eventId) {
        repository.deleteByUserIdAndEventId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(eventId));
    }

    public List<Occurrence> getByEventId(UUID eventId) {
        return converter.convertEntity(repository.getByEventId(uuidConverter.convertDomain(eventId)));
    }

    public void deleteAllById(Collection<UUID> deletedOccurrences) {
        List<String> ids = deletedOccurrences.stream()
            .map(uuidConverter::convertDomain)
            .toList();

        repository.deleteAllById(ids);
    }

    public Occurrence findByIdValidated(UUID occurrenceId) {
        return findById(occurrenceId)
            .orElseThrow(() -> ExceptionFactory.notFound("Occurrence not found with occurrenceId: " + occurrenceId));
    }

    public Optional<Occurrence> findById(UUID occurrenceId) {
        return findById(uuidConverter.convertDomain(occurrenceId));
    }

    public List<Occurrence> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }
}
