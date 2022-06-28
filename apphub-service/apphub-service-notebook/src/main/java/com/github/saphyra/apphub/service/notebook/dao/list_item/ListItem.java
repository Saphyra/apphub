package com.github.saphyra.apphub.service.notebook.dao.list_item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ListItem {
    @NonNull
    private final UUID listItemId;

    @NonNull
    private final UUID userId;

    private UUID parent;

    @NonNull
    private ListItemType type;

    @NonNull
    private String title;

    private boolean pinned;
    private boolean archived;
}
