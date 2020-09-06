package com.github.saphyra.apphub.integration.backend.model.notebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EditChecklistItemRequest {
    private String title;
    private List<ChecklistItemNodeRequest> nodes;
}
