package com.github.saphyra.apphub.service.notebook.dao.list_item.table.head;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableHeadFactory {
    private final IdGenerator idGenerator;

    public TableHead clone(TableHead tableHead) {
        return create(tableHead.getIndex());
    }

    public TableHead create(int columnIndex) {
        return TableHead.builder()
            .tableHeadId(idGenerator.randomUuid())
            .index(columnIndex)
            .build();
    }
}
