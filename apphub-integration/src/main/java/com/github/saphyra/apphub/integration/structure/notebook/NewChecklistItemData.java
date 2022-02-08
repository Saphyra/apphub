package com.github.saphyra.apphub.integration.structure.notebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NewChecklistItemData {
    private String content;
    private boolean checked;
}
