package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao;

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
public class ToolDao extends AbstractDao<ToolEntity, Tool, String, ToolRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    ToolDao(ToolConverter converter, ToolRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<Tool> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public Tool findByIdValidated(UUID toolId) {
        return findById(uuidConverter.convertDomain(toolId))
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Tool not found by id " + toolId));
    }

    public void deleteByUserIdAndToolId(UUID userId, UUID toolId) {
        repository.deleteByUserIdAndToolId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(toolId));
    }
}
