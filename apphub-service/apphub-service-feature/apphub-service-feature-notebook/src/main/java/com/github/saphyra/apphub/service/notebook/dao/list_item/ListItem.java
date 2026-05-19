package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class ListItem {
    @NonNull
    private final UUID listItemId;

    @NonNull
    private final UUID userId;

    @Nullable //If parent is root
    private UUID parent;

    @NonNull
    private ListItemType type;

    @NonNull
    private String title;

    private boolean pinned;

    private boolean archived;

    @Nullable
    //StoredFileId if type == IMAGE/FILE, URL if type == LINK
    private String data;
}
