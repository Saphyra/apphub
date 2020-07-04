package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ChildrenOfCategoryResponse {
    private final UUID parent;

    private final String title;

    @NonNull
    private final List<NotebookView> children;
}
