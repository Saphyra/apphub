package com.github.saphyra.apphub.integration.frontend.model.notebook;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewChecklistItemData {
    private String content;
    private boolean checked;
}
