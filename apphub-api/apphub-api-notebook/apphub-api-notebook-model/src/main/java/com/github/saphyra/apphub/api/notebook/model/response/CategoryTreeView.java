package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CategoryTreeView {
    @NonNull
    private final UUID categoryId;

    @NonNull
    private final String title;

    private final boolean archived;

    @NonNull
    private final List<CategoryTreeView> children;
}
