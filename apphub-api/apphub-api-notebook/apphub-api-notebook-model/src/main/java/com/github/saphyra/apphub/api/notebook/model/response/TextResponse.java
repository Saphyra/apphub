package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
public class TextResponse {
    @NonNull
    private final UUID textId;

    private final UUID parent;

    @NonNull
    private final String title;

    @NonNull
    private final String content;
}
