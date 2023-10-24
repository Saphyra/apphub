package com.github.saphyra.apphub.service.notebook.migration.table;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class UnencryptedListItem {
    @NonNull
    private final UUID listItemId;

    @NonNull
    private final UUID userId;

    private UUID parent;

    @NonNull
    private ListItemType type;

    @NonNull
    private String title;

    private String pinned;

    private String archived;
}
