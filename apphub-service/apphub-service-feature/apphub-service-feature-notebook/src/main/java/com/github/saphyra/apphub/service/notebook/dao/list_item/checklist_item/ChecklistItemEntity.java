package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
class ChecklistItemEntity {
    private String listItemId;
    private String checklistItemId;
    private String checked;
    private String index;
}
