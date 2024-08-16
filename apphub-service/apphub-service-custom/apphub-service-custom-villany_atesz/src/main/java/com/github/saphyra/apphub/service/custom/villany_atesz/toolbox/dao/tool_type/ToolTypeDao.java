package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type;

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
public class ToolTypeDao extends AbstractDao<ToolTypeEntity, ToolType, String, ToolTypeRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    ToolTypeDao(ToolTypeConverter converter, ToolTypeRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public boolean exists(UUID toolTypeId) {
        return repository.existsById(uuidConverter.convertDomain(toolTypeId));
    }

    public ToolType findByIdValidated(UUID toolTypeId) {
        return findById(toolTypeId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ToolType not found by id " + toolTypeId));
    }

    public Optional<ToolType> findById(UUID toolTypeId) {
        return findById(uuidConverter.convertDomain(toolTypeId));
    }

    public List<ToolType> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }
}
