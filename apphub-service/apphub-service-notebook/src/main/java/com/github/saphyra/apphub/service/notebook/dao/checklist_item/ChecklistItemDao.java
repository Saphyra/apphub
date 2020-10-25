package com.github.saphyra.apphub.service.notebook.dao.checklist_item;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ChecklistItemDao extends AbstractDao<ChecklistItemEntity, ChecklistItem, String, ChecklistItemRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public ChecklistItemDao(ChecklistItemConverter converter, ChecklistItemRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<ChecklistItem> getByParent(UUID parent) {
        return converter.convertEntity(repository.getByParent(uuidConverter.convertDomain(parent)));
    }

    public ChecklistItem findByIdValidated(UUID checklistItemId) {
        return findById(uuidConverter.convertDomain(checklistItemId))
            .orElseThrow(() -> new NotFoundException(new ErrorMessage(ErrorCode.LIST_ITEM_NOT_FOUND.name()), "ChecklistItem not found with id " + checklistItemId));
    }
}
