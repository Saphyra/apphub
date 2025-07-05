package com.github.saphyra.apphub.service.notebook.dao.pin.group;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PinGroupDao extends AbstractDao<PinGroupEntity, PinGroup, String, PinGroupRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public PinGroupDao(PinGroupConverter converter, PinGroupRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<PinGroup> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public PinGroup findByIdValidated(UUID pinGroupId) {
        return findById(pinGroupId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "PinGroup not found by id " + pinGroupId));
    }

    private Optional<PinGroup> findById(UUID pinGroupId) {
        return findById(uuidConverter.convertDomain(pinGroupId));
    }

    public void deleteById(UUID pinGroupId) {
        deleteById(uuidConverter.convertDomain(pinGroupId));
    }
}
