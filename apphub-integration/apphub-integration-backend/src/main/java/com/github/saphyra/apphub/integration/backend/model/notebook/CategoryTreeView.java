package com.github.saphyra.apphub.integration.backend.model.notebook;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CategoryTreeView {
    private UUID categoryId;
    private String title;
    private List<CategoryTreeView> children;
}
