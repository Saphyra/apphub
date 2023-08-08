package com.github.saphyra.apphub.integration.structure.api.notebook;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChecklistItemResponse {
    private UUID checklistItemId;
    private String content;
    private Boolean checked;
    private Integer order;
}
