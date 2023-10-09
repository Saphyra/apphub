package com.github.saphyra.apphub.service.notebook.migration.checklist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class UnencryptedChecklistItem {
    @NonNull
    private final UUID checklistItemId;

    @NonNull
    private final UUID userId;

    @NonNull
    private final UUID parent;

    @NonNull
    private String order;

    @NonNull
    private String checked;
}
