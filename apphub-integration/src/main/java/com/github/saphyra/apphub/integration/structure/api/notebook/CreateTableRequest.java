package com.github.saphyra.apphub.integration.structure.api.notebook;

import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableHeadModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableRowModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTableRequest {
    private String title;
    private UUID parent;
    private ListItemType listItemType;
    private List<TableHeadModel> tableHeads;
    private List<TableRowModel> rows;
}
