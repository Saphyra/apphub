package com.github.saphyra.apphub.service.notebook.dao.table.row;

import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
@ForRemoval("notebook-redesign")
public class ChecklistTableRow {
    private final UUID rowId;
    private final UUID userId;
    private final UUID parent;
    private final Integer rowIndex;
    private boolean checked;
}
