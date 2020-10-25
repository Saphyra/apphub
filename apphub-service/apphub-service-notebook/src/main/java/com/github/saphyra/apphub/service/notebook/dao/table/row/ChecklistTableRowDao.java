package com.github.saphyra.apphub.service.notebook.dao.table.row;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.dao.AbstractDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class ChecklistTableRowDao extends AbstractDao<ChecklistTableRowEntity, ChecklistTableRow, String, ChecklistTableRowRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public ChecklistTableRowDao(ChecklistTableRowConverter converter, ChecklistTableRowRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public Optional<ChecklistTableRow> findByParentAndRowIndex(UUID parent, int rowIndex) {
        return converter.convertEntity(repository.findByParentAndRowIndex(uuidConverter.convertDomain(parent), rowIndex));
    }

    public List<ChecklistTableRow> getByParent(UUID parent) {
        return converter.convertEntity(repository.getByParent(uuidConverter.convertDomain(parent)));
    }

    public void deleteByParentAndRowIndexGreaterThanEqual(UUID parent, int rowIndex) {
        repository.deleteByParentAndRowIndexGreaterThanEqual(uuidConverter.convertDomain(parent), rowIndex);
    }

    public void deleteByParent(UUID listItemId) {
        repository.deleteByParent(uuidConverter.convertDomain(listItemId));
    }
}
