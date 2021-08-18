package com.github.saphyra.apphub.service.notebook.dao.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class Content {
    @NonNull
    private final UUID contentId;

    @NonNull
    private final UUID userId;

    @NonNull
    private final UUID parent;

    private final UUID listItemId;

    @NonNull
    private String content;
}
