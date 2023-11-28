package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface ColumnDataService {
    boolean canProcess(ColumnType columnType);

    @Transactional
    Optional<TableFileUploadResponse> save(UUID userId, UUID listItemId, UUID rowId, TableColumnModel model);

    Object getData(UUID columnId);

    @Transactional
    void delete(Dimension column);

    @Transactional
    Optional<TableFileUploadResponse> edit(ListItem listItem, UUID rowId, TableColumnModel model);

    @Transactional
    void clone(ListItem clone, UUID rowId, Dimension originalColumn);

    void validateData(Object data);
}
