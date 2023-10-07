package com.github.saphyra.apphub.service.notebook.service.checklist_deprecated;

import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistItemStatusUpdateService {
    private final ChecklistItemDao checklistItemDao;

    public void update(UUID checklistItemId, Boolean checked) {
        ChecklistItem checklistItem = checklistItemDao.findByIdValidated(checklistItemId);
        checklistItem.setChecked(checked);
        checklistItemDao.save(checklistItem);
    }
}
