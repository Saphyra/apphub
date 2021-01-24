package com.github.saphyra.apphub.service.utils.log_formatter.repository;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
//TODO unit test
public class LogParameterVisibilityDao extends AbstractDao<LogParameterVisibilityEntity, LogParameterVisibility, String, LogParameterVisibilityRepository> {
    private final UuidConverter uuidConverter;

    public LogParameterVisibilityDao(LogParameterVisibilityConverter converter, LogParameterVisibilityRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<LogParameterVisibility> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public Optional<LogParameterVisibility> findById(UUID id) {
        return converter.convertEntity(repository.findById(uuidConverter.convertDomain(id)));
    }
}
