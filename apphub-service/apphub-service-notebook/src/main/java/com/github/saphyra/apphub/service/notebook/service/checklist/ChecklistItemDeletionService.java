package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistItemDeletionService {
    private final ChecklistItemDao checklistItemDao;
    private final ContentDao contentDao;

    @Transactional
    public void delete(UUID checklistItemId) {
        ChecklistItem checklistItem = checklistItemDao.findByIdValidated(checklistItemId);
        delete(checklistItem);
    }

    @Transactional
    public void deleteCheckedItems(UUID listItemId) {
        checklistItemDao.getByParent(listItemId)
            .stream()
            .filter(ChecklistItem::getChecked)
            .forEach(this::delete);
    }

    private void delete(ChecklistItem checklistItem) {
        contentDao.deleteByParent(checklistItem.getChecklistItemId());
        checklistItemDao.delete(checklistItem);
    }
}
