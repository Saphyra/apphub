package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class ChecklistItem {
    @NonNull
    private final UUID userId;
    @NonNull
    private final UUID listItemId;
    @NonNull
    private final UUID checklistItemId; //ContentId
    private boolean checked;
    private int index;
}
