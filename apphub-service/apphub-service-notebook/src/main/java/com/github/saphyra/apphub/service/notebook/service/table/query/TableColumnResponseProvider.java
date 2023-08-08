package com.github.saphyra.apphub.service.notebook.service.table.query;

import com.github.saphyra.apphub.api.notebook.model.response.TableColumnResponse;

import java.util.List;
import java.util.UUID;

public interface TableColumnResponseProvider<T> {
    List<TableColumnResponse<T>> fetchTableColumns(UUID listItemId);
}
