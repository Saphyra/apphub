package com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao;

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
public class DeprecatedLabelDao extends AbstractDao<DeprecatedLabelEntity, DeprecatedLabel, String, DeprecatedLabelRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    DeprecatedLabelDao(DeprecatedLabelConverter converter, DeprecatedLabelRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public boolean existsById(UUID labelId) {
        return repository.existsById(uuidConverter.convertDomain(labelId));
    }

    public List<DeprecatedLabel> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public void deleteByUserIdAndLabelId(UUID userId, UUID labelId) {
        repository.deleteByUserIdAndLabelId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(labelId));
    }

    public DeprecatedLabel findByIdValidated(UUID labelId) {
        return findById(labelId)
            .orElseThrow(() -> ExceptionFactory.notFound("DeprecatedLabel not found with id: " + labelId));
    }

    private Optional<DeprecatedLabel> findById(UUID labelId) {
        return findById(uuidConverter.convertDomain(labelId));
    }
}
