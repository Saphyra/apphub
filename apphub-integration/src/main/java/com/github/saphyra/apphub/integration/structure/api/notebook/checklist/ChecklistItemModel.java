package com.github.saphyra.apphub.integration.structure.api.notebook.checklist;

import com.github.saphyra.apphub.integration.structure.api.notebook.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChecklistItemModel {
    private UUID checklistItemId;
    private Integer index;
    private Boolean checked;
    private String content;
    private ItemType type;
}
