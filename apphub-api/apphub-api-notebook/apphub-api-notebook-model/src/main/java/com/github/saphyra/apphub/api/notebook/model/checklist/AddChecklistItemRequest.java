package com.github.saphyra.apphub.api.notebook.model.checklist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AddChecklistItemRequest {
    private Integer index;
    private String content;
}
