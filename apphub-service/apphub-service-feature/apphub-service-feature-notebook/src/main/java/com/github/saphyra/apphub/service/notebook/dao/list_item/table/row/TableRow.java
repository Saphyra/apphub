package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class TableRow {
    @NonNull
    private final UUID listItemId;
    @NonNull
    private final UUID tableRowId;
    private int index;
    @NonNull
    private Boolean checked;
    @NonNull
    private List<TableColumn> columns;
}
