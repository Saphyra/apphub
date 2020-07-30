package com.github.saphyra.apphub.service.notebook.dao.checklist_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
public class ChecklistItem {
    @NonNull
    private final UUID checklistItemId;

    @NonNull
    private final UUID userId;

    @NonNull
    private final UUID parent;

    @NonNull
    private Integer order;

    @NonNull
    private Boolean checked;
}
