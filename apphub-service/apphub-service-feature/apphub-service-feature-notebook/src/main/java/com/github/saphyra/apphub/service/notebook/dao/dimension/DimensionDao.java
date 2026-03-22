package com.github.saphyra.apphub.service.notebook.dao.dimension;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class DimensionDao extends AbstractDao<DimensionEntity, Dimension, String, DimensionRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public DimensionDao(DimensionConverter converter, DimensionRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<Dimension> getByExternalReference(UUID externalReference) {
        return converter.convertEntity(repository.getByExternalReference(uuidConverter.convertDomain(externalReference)));
    }

    public Dimension findByIdValidated(UUID dimensionId) {
        return findById(dimensionId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Dimension not found with id " + dimensionId));
    }

    private Optional<Dimension> findById(UUID dimensionId) {
        return findById(uuidConverter.convertDomain(dimensionId));
    }
}
