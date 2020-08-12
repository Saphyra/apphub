package com.github.saphyra.apphub.service.notebook.dao.table.join;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TableJoinDao extends AbstractDao<TableJoinEntity, TableJoin, String, TableJoinRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public TableJoinDao(TableJoinConverter converter, TableJoinRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public boolean exists(UUID tableJoinId) {
        return repository.existsById(uuidConverter.convertDomain(tableJoinId));
    }

    public List<TableJoin> getByParent(UUID parent) {
        return converter.convertEntity(repository.getByParent(uuidConverter.convertDomain(parent)));
    }
}
