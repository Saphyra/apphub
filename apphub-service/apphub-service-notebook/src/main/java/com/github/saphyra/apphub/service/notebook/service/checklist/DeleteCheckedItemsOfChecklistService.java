package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.query.ChecklistQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class DeleteCheckedItemsOfChecklistService {
    private final ChecklistQueryService checklistQueryService;
    private final DimensionDao dimensionDao;
    private final CheckedItemDao checkedItemDao;
    private final ChecklistItemDeletionService checklistItemDeletionService;

    @Transactional
    public ChecklistResponse deleteCheckedItems(UUID listItemId) {
        dimensionDao.getByExternalReference(listItemId)
            .stream()
            .map(dimension -> checkedItemDao.findByIdValidated(dimension.getDimensionId()))
            .filter(checkedItem -> Boolean.TRUE.equals(checkedItem.getChecked()))
            .forEach(checkedItem -> checklistItemDeletionService.deleteChecklistItem(checkedItem.getCheckedItemId()));

        return checklistQueryService.getChecklistResponse(listItemId);
    }
}
