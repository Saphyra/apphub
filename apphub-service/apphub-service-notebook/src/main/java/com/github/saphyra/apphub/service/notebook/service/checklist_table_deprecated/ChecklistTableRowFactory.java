package com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistTableRowFactory {
    private final IdGenerator idGenerator;

    public ChecklistTableRow create(UUID userId, UUID parent, int rowIndex, boolean checked) {
        return create(idGenerator.randomUuid(), userId, parent, rowIndex, checked);
    }

    public ChecklistTableRow create(UUID rowId, UUID userId, UUID parent, int rowIndex, boolean checked) {
        return ChecklistTableRow.builder()
            .rowId(rowId)
            .userId(userId)
            .parent(parent)
            .rowIndex(rowIndex)
            .checked(checked)
            .build();
    }
}
