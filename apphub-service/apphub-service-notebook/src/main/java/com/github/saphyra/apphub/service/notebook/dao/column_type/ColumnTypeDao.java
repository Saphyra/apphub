package com.github.saphyra.apphub.service.notebook.dao.column_type;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
}
