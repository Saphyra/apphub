package com.github.saphyra.apphub.service.notebook.service.table.column_data.base;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.api.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;

import java.util.Optional;
import java.util.UUID;

public interface ColumnDataService {
    boolean canProcess(ColumnType columnType);

    Optional<TableFileUploadResponse> save(UUID userId, UUID listItemId, UUID rowId, TableColumnModel model);

    Object getData(UUID columnId);

    void delete(Dimension column);

    Optional<TableFileUploadResponse> edit(ListItem listItem, UUID rowId, TableColumnModel model);

    void clone(ListItem clone, UUID rowId, Dimension originalColumn);

    void validateData(Object data);
}
