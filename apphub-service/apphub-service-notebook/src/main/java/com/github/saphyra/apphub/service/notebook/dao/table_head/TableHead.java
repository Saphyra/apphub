package com.github.saphyra.apphub.service.notebook.dao.table_head;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class TableHead {
    @NonNull
    private final UUID tableHeadId;

    @NonNull
    private final UUID userId;

    @NonNull
    private final UUID parent;

    @NonNull
    private Integer columnIndex;
}
