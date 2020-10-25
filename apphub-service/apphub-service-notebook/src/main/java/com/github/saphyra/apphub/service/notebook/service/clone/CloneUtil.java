package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
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
            .checklistItemId(idGenerator.randomUUID())
            .parent(parent)
            .build();
    }

    TableHead clone(UUID parent, TableHead toClone) {
        return toClone.toBuilder()
            .tableHeadId(idGenerator.randomUUID())
            .parent(parent)
            .build();
    }

    TableJoin clone(UUID parent, TableJoin toClone) {
        return toClone.toBuilder()
            .tableJoinId(idGenerator.randomUUID())
            .parent(parent)
            .build();
    }
}
