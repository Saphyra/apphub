package com.github.saphyra.apphub.api.notebook.model.table;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
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
public class CreateTableRequest {
    private String title;
    private UUID parent;
    private ListItemType listItemType;
    private List<TableHeadModel> tableHeads;
    private List<TableRowModel> rows;
}
