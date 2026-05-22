package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TableColumnFactory {
    private final IdGenerator idGenerator;

    public TableColumn create(int index, ColumnType type) {
        return TableColumn.builder()
            .columnId(idGenerator.randomUuid())
            .index(index)
            .type(type)
            .build();
    }
}
