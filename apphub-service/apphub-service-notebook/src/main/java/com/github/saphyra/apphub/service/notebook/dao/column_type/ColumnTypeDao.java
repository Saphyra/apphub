package com.github.saphyra.apphub.service.notebook.dao.column_type;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
//TODO unit test
public class ColumnTypeDao extends AbstractDao<ColumnTypeEntity, ColumnTypeDto, String, ColumnTypeRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public ColumnTypeDao(ColumnTypeConverter converter, ColumnTypeRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public ColumnTypeDto findByIdValidated(UUID columnId) {
        return findById(columnId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ColumnType not found with id " + columnId));
    }

    public Optional<ColumnTypeDto> findById(UUID columnId) {
        return findById(uuidConverter.convertDomain(columnId));
    }

    public void deleteById(UUID columnId) {
        deleteById(uuidConverter.convertDomain(columnId));
    }
}
