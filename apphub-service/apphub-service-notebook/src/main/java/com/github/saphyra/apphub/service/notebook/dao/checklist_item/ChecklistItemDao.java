package com.github.saphyra.apphub.service.notebook.dao.checklist_item;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.migration.checklist.UnencryptedChecklistItem;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Deprecated
public class ChecklistItemDao extends AbstractDao<ChecklistItemEntity, ChecklistItem, String, ChecklistItemRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;
    private final ChecklistItemConverter converter;

    public ChecklistItemDao(ChecklistItemConverter converter, ChecklistItemRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
        this.converter = converter;
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
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.LIST_ITEM_NOT_FOUND, "ChecklistItem not found with id " + checklistItemId));
    }

    //TODO unit test
    @ForRemoval("notebook-redesign")
    public List<UnencryptedChecklistItem> getAllUnencrypted() {
        return this.converter.convertEntityUnencrypted(repository.findAll());
    }

    //TODO unit test
    @ForRemoval("notebook-redesign")
    public List<ChecklistItem> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }
}
