package com.github.saphyra.apphub.service.diary.dao.occurance;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
//TODO unit test
public class OccurrenceDao extends AbstractDao<OccurrenceEntity, Occurrence, String, OccurrenceRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public OccurrenceDao(OccurrenceConverter converter, OccurrenceRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public void deleteVirtualByUserId(UUID userId) {
        repository.deleteByUserIdAndStatus(uuidConverter.convertDomain(userId), OccurrenceStatus.VIRTUAL);
    }

    public List<Occurrence> getByEventId(UUID eventId) {
        return converter.convertEntity(repository.getByEventId(uuidConverter.convertDomain(eventId)));
    }

    public Optional<Occurrence> findById(UUID occurrenceId) {
        return findById(uuidConverter.convertDomain(occurrenceId));
    }

    public Occurrence findByIdValidated(UUID occurrenceId) {
        return findById(occurrenceId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Occurrence not found with id " + occurrenceId));
    }

    public void deleteByEventId(UUID eventId) {
        repository.deleteByEventId(uuidConverter.convertDomain(eventId));
    }
}
