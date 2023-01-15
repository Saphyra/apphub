package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@Builder
public class NotebookView {
    @NonNull
    private final UUID id;

    @NonNull
    private final String title;

    @NonNull
    private final String type;
    private final boolean pinned;
    private final boolean archived;
    private final String value;
    private final UUID parentId;
    private final String parentTitle;
    private final boolean enabled;
}
