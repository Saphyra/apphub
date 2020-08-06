package com.github.saphyra.apphub.service.notebook.dao.table.join;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.converter.Converter;
import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
//TODO unit test
public class TableJoinDao extends AbstractDao<TableJoinEntity, TableJoin, String, TableJoinRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public TableJoinDao(Converter<TableJoinEntity, TableJoin> converter, TableJoinRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }
}
