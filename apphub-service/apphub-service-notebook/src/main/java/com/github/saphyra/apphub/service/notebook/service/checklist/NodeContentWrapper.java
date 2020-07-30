package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class NodeContentWrapper {
    @NonNull
    private final ChecklistItem checklistItem;

    @NonNull
    private final Content content;
}
