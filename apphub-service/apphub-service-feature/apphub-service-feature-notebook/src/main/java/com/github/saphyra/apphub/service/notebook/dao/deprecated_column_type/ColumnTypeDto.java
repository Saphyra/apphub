package com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder(toBuilder = true)
@Deprecated(forRemoval = true)
public class ColumnTypeDto {
    private final UUID columnId;
    private final UUID userId;
    private final ColumnType type;
}
