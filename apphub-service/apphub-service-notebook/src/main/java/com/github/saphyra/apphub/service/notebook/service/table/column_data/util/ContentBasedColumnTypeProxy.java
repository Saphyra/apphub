package com.github.saphyra.apphub.service.notebook.service.table.column_data.util;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ContentBasedColumnTypeProxy {
    private final ContentBasedColumnTypeSaver contentBasedColumnTypeSaver;
    private final ContentBasedColumnTypeEditer contentBasedColumnTypeEditer;
    private final ContentBasedColumnTypeCloner contentBasedColumnTypeCloner;
    private final ContentBasedColumnTypeDeleter contentBasedColumnTypeDeleter;

    public void save(UUID userId, UUID listItemId, UUID rowId, Integer columnIndex, String contentValue, ColumnType columnType) {
        contentBasedColumnTypeSaver.save(userId, listItemId, rowId, columnIndex, contentValue, columnType);
    }

    public void delete(Dimension column) {
        contentBasedColumnTypeDeleter.delete(column);
    }

    public void edit(UUID columnId, Integer columnIndex, String contentValue) {
        contentBasedColumnTypeEditer.edit(columnId, columnIndex, contentValue);
    }

    public void clone(UUID userId, UUID clonedListItemId, UUID rowId, UUID originalColumnId, Integer columnIndex, ColumnType columnType) {
        contentBasedColumnTypeCloner.clone(userId, clonedListItemId, rowId, originalColumnId, columnIndex, columnType);
    }
}
