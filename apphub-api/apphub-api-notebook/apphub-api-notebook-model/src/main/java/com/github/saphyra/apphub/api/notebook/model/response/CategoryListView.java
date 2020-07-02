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
public class CategoryListView {
    @NonNull
    private final UUID categoryId;

    @NonNull
    private final String title;

    @NonNull
    private final List<CategoryListView> children;
}
