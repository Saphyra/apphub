package com.github.saphyra.apphub.integration.structure.api.notebook.checklist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateChecklistRequest {
    private UUID parent;
    private String title;
    private List<ChecklistItemModel> items;
}
