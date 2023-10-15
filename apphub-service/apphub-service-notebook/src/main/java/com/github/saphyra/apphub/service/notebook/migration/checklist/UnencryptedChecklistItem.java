package com.github.saphyra.apphub.service.notebook.migration.checklist;

import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@ForRemoval("notebook-redesign")
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
