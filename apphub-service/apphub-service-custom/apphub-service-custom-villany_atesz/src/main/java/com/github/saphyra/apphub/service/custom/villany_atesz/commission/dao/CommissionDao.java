package com.github.saphyra.apphub.service.custom.villany_atesz.commission.dao;

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
public class CommissionDao extends AbstractDao<CommissionEntity, Commission, String, CommissionRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    CommissionDao(CommissionConverter converter, CommissionRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Commission findByIdValidated(UUID commissionId) {
        return findById(commissionId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Commission not found by id " + commissionId));
    }

    public Optional<Commission> findById(UUID commissionId) {
        return findById(uuidConverter.convertDomain(commissionId));
    }

    public List<Commission> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }
}
