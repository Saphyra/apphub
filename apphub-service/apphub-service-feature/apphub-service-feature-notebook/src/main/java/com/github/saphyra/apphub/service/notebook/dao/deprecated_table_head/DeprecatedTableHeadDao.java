package com.github.saphyra.apphub.service.notebook.dao.deprecated_table_head;

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
@Deprecated(forRemoval = true)
public class DeprecatedTableHeadDao extends AbstractDao<TableHeadEntity, TableHead, String, DeprecatedTableHeadRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public DeprecatedTableHeadDao(DeprecatedTableHeadConverter converter, DeprecatedTableHeadRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public boolean exists(UUID tableHeadId) {
        return repository.existsById(uuidConverter.convertDomain(tableHeadId));
    }

    public List<TableHead> getByParent(UUID parent) {
        return converter.convertEntity(repository.getByParent(uuidConverter.convertDomain(parent)));
    }

    public void deleteById(UUID tableHeadId) {
        deleteById(uuidConverter.convertDomain(tableHeadId));
    }

    public Optional<TableHead> findById(UUID tableHeadId) {
        return findById(uuidConverter.convertDomain(tableHeadId));
    }

    public TableHead findByIdValidated(UUID tableHeadId) {
        return findById(tableHeadId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "TableHead not found with id " + tableHeadId));
    }
}
