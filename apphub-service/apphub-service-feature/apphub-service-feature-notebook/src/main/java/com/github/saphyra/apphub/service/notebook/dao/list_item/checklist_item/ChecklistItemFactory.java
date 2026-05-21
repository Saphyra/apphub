package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ChecklistItemFactory {
    private final IdGenerator idGenerator;

    public ChecklistItem clone(UUID listItemId, ChecklistItem checklistItem) {
        return create(checklistItem.getUserId(), listItemId, checklistItem.isChecked(), checklistItem.getIndex());
    }

    public ChecklistItem create(UUID userId, UUID listItemId, boolean checked, int index) {
        return ChecklistItem.builder()
            .userId(userId)
            .listItemId(listItemId)
            .checklistItemId(idGenerator.randomUuid())
            .checked(checked)
            .index(index)
            .build();
    }
}
