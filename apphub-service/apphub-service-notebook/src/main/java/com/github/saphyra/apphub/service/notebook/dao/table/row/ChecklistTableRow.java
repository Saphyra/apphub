package com.github.saphyra.apphub.service.notebook.dao.table.row;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChecklistTableRow {
    private final UUID rowId;
    private final UUID userId;
    private final UUID parent;
    private final Integer rowIndex;
    private boolean checked;
}
