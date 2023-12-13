package com.github.saphyra.apphub.api.notebook.model.response;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
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
    private ListItemType listItemType;
    private List<NotebookView> children;
}
