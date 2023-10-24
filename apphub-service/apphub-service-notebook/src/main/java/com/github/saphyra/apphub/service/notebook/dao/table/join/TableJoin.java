package com.github.saphyra.apphub.service.notebook.dao.table.join;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Data
@Builder(toBuilder = true)
@ForRemoval("notebook-redesign")
public class TableJoin {
    @NonNull
    private final UUID tableJoinId;

    @NonNull
    private final UUID userId;

    @NonNull
    private final UUID parent;

    @NonNull
    private Integer rowIndex;

    @NonNull
    private Integer columnIndex;

    @NonNull
    private ColumnType columnType;
}
