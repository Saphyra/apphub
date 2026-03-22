package com.github.saphyra.apphub.service.notebook.dao.table_head;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableHeadFactory {
    private final IdGenerator idGenerator;

    public TableHead create(UUID userId, UUID parent, Integer columnIndex) {
        return TableHead.builder()
                .tableHeadId(idGenerator.randomUuid())
                .userId(userId)
                .parent(parent)
                .columnIndex(columnIndex)
                .build();
    }
}
