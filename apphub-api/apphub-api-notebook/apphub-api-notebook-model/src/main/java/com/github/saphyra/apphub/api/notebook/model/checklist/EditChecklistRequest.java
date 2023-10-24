package com.github.saphyra.apphub.api.notebook.model.checklist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EditChecklistRequest {
    private String title;
    private List<ChecklistItemModel> items;
}
