package com.github.saphyra.apphub.integration.backend.model.notebook;

import lombok.*;

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
