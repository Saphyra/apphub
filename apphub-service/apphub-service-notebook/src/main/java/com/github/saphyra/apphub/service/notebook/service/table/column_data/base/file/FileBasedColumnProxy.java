package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileBasedColumnProxy {
    private final FileBasedColumnSaver fileBasedColumnSaver;
    private final FileBasedColumnDeleter fileBasedColumnDeleter;
    private final FileBasedColumnEditer fileBasedColumnEditer;
    private final FileBasedColumnCloner fileBasedColumnCloner;

    Optional<TableFileUploadResponse> save(UUID userId, UUID rowId, TableColumnModel model, ColumnType columnType) {
        return fileBasedColumnSaver.save(userId, rowId, model, columnType);
    }

    public void delete(Dimension column) {
        fileBasedColumnDeleter.delete(column);
    }

    public Optional<TableFileUploadResponse> edit(ListItem listItem, UUID rowId, TableColumnModel model) {
        return fileBasedColumnEditer.edit(listItem, rowId, model);
    }

    public void clone(ListItem clone, UUID rowId, Dimension originalColumn, ColumnType columnType) {
        fileBasedColumnCloner.clone(clone, rowId, originalColumn, columnType);
    }
}
