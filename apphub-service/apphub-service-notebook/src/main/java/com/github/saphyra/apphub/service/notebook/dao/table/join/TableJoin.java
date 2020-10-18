package com.github.saphyra.apphub.service.notebook.dao.table.join;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Data
@Builder(toBuilder = true)
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
}
