package com.github.saphyra.apphub.integration.structure.api.notebook.table;

import com.github.saphyra.apphub.integration.structure.api.notebook.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class TableHeadModel {
    private UUID tableHeadId;
    private Integer columnIndex;
    private String content;
    private ItemType type;
}
