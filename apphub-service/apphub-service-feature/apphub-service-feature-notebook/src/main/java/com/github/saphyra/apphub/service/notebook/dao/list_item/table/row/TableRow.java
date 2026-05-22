package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

import jakarta.annotation.Nullable;
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
    private final UUID userId;
    @NonNull
    private final UUID listItemId;
    @NonNull
    private final UUID tableRowId;
    private int index;
    @Nullable //Filled when checklist table
    private Boolean checked;
    @NonNull
    private List<TableColumn> columns;
}
