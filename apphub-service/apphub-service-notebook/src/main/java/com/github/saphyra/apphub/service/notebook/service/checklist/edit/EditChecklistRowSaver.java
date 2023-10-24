package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.service.notebook.service.checklist.create.ChecklistItemCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class EditChecklistRowSaver {
    private final ChecklistItemCreationService checklistItemCreationService;
    private final EditChecklistRowUpdater editChecklistRowUpdater;

    void saveItems(UUID userId, UUID listItemId, List<ChecklistItemModel> items) {
        items.forEach(item -> saveItem(userId, listItemId, item));
    }

    private void saveItem(UUID userId, UUID listItemId, ChecklistItemModel item) {
        if (item.getType() == ItemType.EXISTING) {
            editChecklistRowUpdater.updateExistingChecklistItem(item);
        } else {
            checklistItemCreationService.create(userId, listItemId, item);
        }
    }
}
