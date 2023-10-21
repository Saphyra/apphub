package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistStatusUpdateService {
    private final CheckedItemDao checkedItemDao;

    public void updateStatus(UUID checklistItemId, Boolean value) {
        CheckedItem checkedItem = checkedItemDao.findByIdValidated(checklistItemId);

        checkedItem.setChecked(value);

        checkedItemDao.save(checkedItem);
    }
}
