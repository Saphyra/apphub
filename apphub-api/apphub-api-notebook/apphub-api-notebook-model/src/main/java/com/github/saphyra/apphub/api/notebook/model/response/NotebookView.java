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
}
