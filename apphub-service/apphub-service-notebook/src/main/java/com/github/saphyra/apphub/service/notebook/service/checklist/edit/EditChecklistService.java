package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.api.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
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
//TODO split
//TODO unit test
public class EditChecklistService {
    private final EditChecklistRequestValidator editChecklistRequestValidator;
    private final ChecklistQueryService checklistQueryService;
    private final DimensionDao dimensionDao;
    private final ChecklistItemDeletionService checklistItemDeletionService;
    private final ChecklistItemCreationService checklistItemCreationService;
    private final CheckedItemDao checkedItemDao;
    private final ContentDao contentDao;
    private final ListItemDao listItemDao;

    @Transactional
    public ChecklistResponse edit(UUID userId, UUID listItemId, EditChecklistRequest request) {
        editChecklistRequestValidator.validate(request);

        ListItem listItem = listItemDao.findByIdValidated(listItemId);
        listItem.setTitle(request.getTitle());
        listItemDao.save(listItem);

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
        items.forEach(item -> saveItem(userId, listItemId, item));
    }

    private void saveItem(UUID userId, UUID listItemId, ChecklistItemModel item) {
        if (item.getType() == ItemType.EXISTING) {
            updateExistingChecklistItem(item);
        } else {
            checklistItemCreationService.create(userId, listItemId, item);
        }
    }

    private void updateExistingChecklistItem(ChecklistItemModel item) {
        Dimension dimension = dimensionDao.findByIdValidated(item.getChecklistItemId());
        dimension.setIndex(item.getIndex());
        dimensionDao.save(dimension);

        CheckedItem checkedItem = checkedItemDao.findByIdValidated(dimension.getDimensionId());
        checkedItem.setChecked(item.getChecked());
        checkedItemDao.save(checkedItem);

        Content content = contentDao.findByParentValidated(dimension.getDimensionId());
        content.setContent(item.getContent());
        contentDao.save(content);
    }
}
