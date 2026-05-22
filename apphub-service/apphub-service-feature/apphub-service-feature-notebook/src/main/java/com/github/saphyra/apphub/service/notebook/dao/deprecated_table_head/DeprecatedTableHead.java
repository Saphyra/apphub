package com.github.saphyra.apphub.service.notebook.dao.deprecated_table_head;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
public class DeprecatedTableHead {
    @NonNull
    private UUID tableHeadId;

    @NonNull
    private UUID userId;

    @NonNull
    private UUID parent;

    @NonNull
    private Integer columnIndex;
}
