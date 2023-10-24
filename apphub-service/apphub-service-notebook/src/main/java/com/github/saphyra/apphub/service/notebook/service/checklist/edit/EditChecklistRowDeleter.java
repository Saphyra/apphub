package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class EditChecklistRowDeleter {
    private final DimensionDao dimensionDao;
    private final ChecklistItemDeletionService checklistItemDeletionService;

    void deleteRemovedItems(UUID listItemId, List<ChecklistItemModel> items) {
        List<UUID> existingItems = items.stream()
            .filter(checklistItemModel -> checklistItemModel.getType() == ItemType.EXISTING)
            .map(ChecklistItemModel::getChecklistItemId)
            .toList();

        dimensionDao.getByExternalReference(listItemId)
            .stream()
            .filter(dimension -> !existingItems.contains(dimension.getDimensionId()))
            .forEach(dimension -> checklistItemDeletionService.deleteChecklistItem(dimension.getDimensionId()));
    }
}
