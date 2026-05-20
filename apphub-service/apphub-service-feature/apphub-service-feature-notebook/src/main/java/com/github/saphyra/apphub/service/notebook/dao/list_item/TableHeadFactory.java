package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TableHeadFactory {
    private final IdGenerator idGenerator;

    public TableHead clone(UUID listItemId, TableHead tableHead) {
        return create(tableHead.getUserId(), listItemId, tableHead.getIndex());
    }

    public TableHead create(UUID userId, UUID listItemId, int columnIndex) {
        return TableHead.builder()
            .userId(userId)
            .listItemId(listItemId)
            .tableHeadId(idGenerator.randomUuid())
            .index(columnIndex)
            .build();
    }
}
