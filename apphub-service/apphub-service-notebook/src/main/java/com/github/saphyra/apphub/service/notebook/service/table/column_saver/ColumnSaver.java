package com.github.saphyra.apphub.service.notebook.service.table.column_saver;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;

import java.util.Optional;
import java.util.UUID;

public interface ColumnSaver {
    boolean canProcess(ColumnType columnType);

    Optional<TableFileUploadResponse> save(UUID userId, UUID listItemId, Dimension row, TableColumnModel model);
}
