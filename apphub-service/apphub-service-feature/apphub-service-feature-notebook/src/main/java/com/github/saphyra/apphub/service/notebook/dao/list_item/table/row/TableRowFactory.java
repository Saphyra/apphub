package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableRowFactory {
    private final IdGenerator idGenerator;

    public TableRow create(UUID listItemId, Integer index, Boolean checked, List<TableColumn> columns) {
        return TableRow.builder()
            .listItemId(listItemId)
            .tableRowId(idGenerator.randomUuid())
            .index(index)
            .checked(checked)
            .columns(columns)
            .build();
    }
}
