package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
@NoArgsConstructor
public class TableColumn {
    @NonNull
    private UUID columnId;
    private int index;
    @NonNull
    private ColumnType type;
}
