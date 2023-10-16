package com.github.saphyra.apphub.integration.structure.api.notebook.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableResponse {
    private String title;
    private UUID parent;
    private List<TableHeadModel> tableHeads;
    private List<TableRowModel> rows;
}
