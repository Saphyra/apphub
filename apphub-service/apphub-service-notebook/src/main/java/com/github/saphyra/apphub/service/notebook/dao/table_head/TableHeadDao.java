package com.github.saphyra.apphub.service.notebook.dao.table_head;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class TableHeadDao extends AbstractDao<TableHeadEntity, TableHead, String, TableHeadRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public TableHeadDao(TableHeadConverter converter, TableHeadRepository repository, UuidConverter uuidConverter) {
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
}
