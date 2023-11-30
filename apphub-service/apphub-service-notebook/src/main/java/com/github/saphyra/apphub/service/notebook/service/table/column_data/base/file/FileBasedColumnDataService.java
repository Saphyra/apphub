package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.file;

import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.ColumnDataService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class FileBasedColumnDataService implements ColumnDataService {
    protected final ColumnType columnType;
    private final FileBasedColumnProxy proxy;
    private final FileDao fileDao;

    @Override
    public boolean canProcess(ColumnType columnType) {
        return columnType == this.columnType;
    }

    @Override
    public Optional<TableFileUploadResponse> save(UUID userId, UUID listItemId, UUID rowId, TableColumnModel model) {
        return proxy.save(userId, rowId, model);
    }

    @Override
    public Object getData(UUID columnId) {
        UUID storedFileId = fileDao.findByParentValidated(columnId)
            .getStoredFileId();
        return FileMetadata.builder()
            .storedFileId(storedFileId)
            .build();
    }

    @Override
    public void delete(Dimension column) {
        proxy.delete(column);
    }

    @Override
    public Optional<TableFileUploadResponse> edit(ListItem listItem, UUID rowId, TableColumnModel model) {
        return proxy.edit(listItem, rowId, model);
    }

    @Override
    public void clone(ListItem clone, UUID rowId, Dimension originalColumn) {
        proxy.clone(clone, rowId, originalColumn);
    }
}
