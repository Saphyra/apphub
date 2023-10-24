package com.github.saphyra.apphub.api.notebook.model.table;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class TableRowModel {
    private UUID rowId;
    private Integer rowIndex;
    private Boolean checked;
    private ItemType itemType;
    private List<TableColumnModel> columns;
}
