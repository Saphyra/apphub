package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentBasedColumnProxy {
    private final ContentBasedColumnSaver contentBasedColumnSaver;
    private final ContentBasedColumnEditer contentBasedColumnEditer;
    private final ContentBasedColumnCloner contentBasedColumnCloner;
    private final ContentBasedColumnDeleter contentBasedColumnDeleter;

    void save(UUID userId, UUID listItemId, UUID rowId, Integer columnIndex, String contentValue, ColumnType columnType) {
        contentBasedColumnSaver.save(userId, listItemId, rowId, columnIndex, contentValue, columnType);
    }

    void delete(Dimension column) {
        contentBasedColumnDeleter.delete(column);
    }

    void edit(UUID columnId, Integer columnIndex, String contentValue) {
        contentBasedColumnEditer.edit(columnId, columnIndex, contentValue);
    }

    void clone(UUID userId, UUID clonedListItemId, UUID rowId, UUID originalColumnId, Integer columnIndex, ColumnType columnType) {
        contentBasedColumnCloner.clone(userId, clonedListItemId, rowId, originalColumnId, columnIndex, columnType);
    }
}
