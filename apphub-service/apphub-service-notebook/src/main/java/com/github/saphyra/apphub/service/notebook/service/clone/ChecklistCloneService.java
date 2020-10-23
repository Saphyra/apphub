package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ChecklistCloneService {
    private final ChecklistItemDao checklistItemDao;
    private final ChecklistItemCloneService checklistItemCloneService;

    void clone(ListItem original, ListItem clone) {
        checklistItemDao.getByParent(original.getListItemId())
            .forEach(checklistItem -> checklistItemCloneService.clone(clone, checklistItem));
    }
}
