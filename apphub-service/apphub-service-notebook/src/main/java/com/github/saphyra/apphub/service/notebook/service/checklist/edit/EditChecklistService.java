package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.api.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemDeletionService;
import com.github.saphyra.apphub.service.notebook.service.checklist.create.ChecklistItemCreationService;
import com.github.saphyra.apphub.service.notebook.service.checklist.query.ChecklistQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EditChecklistService {
    private final EditChecklistRequestValidator editChecklistRequestValidator;
    private final ChecklistQueryService checklistQueryService;
    private final DimensionDao dimensionDao;
    private final ChecklistItemDeletionService checklistItemDeletionService;
    private final ChecklistItemCreationService checklistItemCreationService;

    @Transactional
    public ChecklistResponse edit(UUID userId, UUID listItemId, EditChecklistRequest request) {
        editChecklistRequestValidator.validate(request);

        deleteRemovedItems(listItemId, request.getItems());
        saveItems(userId, listItemId, request.getItems());

        return checklistQueryService.getChecklistResponse(listItemId);
    }

    private void deleteRemovedItems(UUID listItemId, List<ChecklistItemModel> items) {
        List<UUID> existingItems = items.stream()
            .filter(checklistItemModel -> checklistItemModel.getType() == ItemType.EXISTING)
            .map(ChecklistItemModel::getChecklistItemId)
            .toList();

        dimensionDao.getByExternalReference(listItemId)
            .stream()
            .filter(dimension -> !existingItems.contains(dimension.getDimensionId()))
            .forEach(dimension -> checklistItemDeletionService.deleteChecklistItem(dimension.getDimensionId()));
    }

    private void saveItems(UUID userId, UUID listItemId, List<ChecklistItemModel> items) {
        items.forEach(item -> checklistItemCreationService.create(userId, listItemId, item));
    }
}
