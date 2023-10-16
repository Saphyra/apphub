package com.github.saphyra.apphub.integration.structure.api.notebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class TableColumnModel {
    private UUID columnId;
    private Integer columnIndex;
    private ColumnType columnType;
    private ItemType itemType;
    private Object data;
}
