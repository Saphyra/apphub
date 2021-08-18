package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ChecklistItemCloneService {
    private final ChecklistItemDao checklistItemDao;
    private final CloneUtil cloneUtil;
    private final ContentDao contentDao;
    private final ContentFactory contentFactory;

    void clone(ListItem clone, ChecklistItem checklistItem) {
        ChecklistItem checklistItemClone = cloneUtil.clone(clone.getListItemId(), checklistItem);
        checklistItemDao.save(checklistItemClone);

        Content checklistItemContent = contentDao.findByParentValidated(checklistItem.getChecklistItemId());
        Content checklistItemContentClone = contentFactory.create(clone.getListItemId(), checklistItemClone.getChecklistItemId(), clone.getUserId(), checklistItemContent.getContent());
        contentDao.save(checklistItemContentClone);
    }
}
