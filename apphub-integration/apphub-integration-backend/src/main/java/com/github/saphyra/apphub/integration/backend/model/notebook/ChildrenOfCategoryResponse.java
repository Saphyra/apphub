package com.github.saphyra.apphub.integration.backend.model.notebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildrenOfCategoryResponse {
    private UUID parent;
    private String title;
    private List<NotebookView> children;
}
