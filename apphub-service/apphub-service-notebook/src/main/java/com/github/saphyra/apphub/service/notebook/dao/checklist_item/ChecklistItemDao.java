package com.github.saphyra.apphub.service.notebook.dao.checklist_item;

import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.dao.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ChecklistItemDao extends AbstractDao<ChecklistItemEntity, ChecklistItem, String, ChecklistItemRepository> {
    private final UuidConverter uuidConverter;

    public ChecklistItemDao(ChecklistItemConverter converter, ChecklistItemRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<ChecklistItem> getByParent(UUID parent) {
        return converter.convertEntity(repository.getByParent(uuidConverter.convertDomain(parent)));
    }
}
