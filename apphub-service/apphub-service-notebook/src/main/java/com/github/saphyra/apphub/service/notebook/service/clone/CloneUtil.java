package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
class CloneUtil {
    private final IdGenerator idGenerator;

    ChecklistItem clone(UUID parent, ChecklistItem toClone) {
        return toClone.toBuilder()
            .checklistItemId(idGenerator.randomUuid())
            .parent(parent)
            .build();
    }

    TableHead clone(UUID parent, TableHead toClone) {
        return toClone.toBuilder()
            .tableHeadId(idGenerator.randomUuid())
            .parent(parent)
            .build();
    }

    TableJoin clone(UUID parent, TableJoin toClone) {
        return toClone.toBuilder()
            .tableJoinId(idGenerator.randomUuid())
            .parent(parent)
            .build();
    }

    ChecklistTableRow clone(UUID parent, ChecklistTableRow toClone) {
        return toClone.toBuilder()
            .rowId(idGenerator.randomUuid())
            .parent(parent)
            .build();
    }
}
