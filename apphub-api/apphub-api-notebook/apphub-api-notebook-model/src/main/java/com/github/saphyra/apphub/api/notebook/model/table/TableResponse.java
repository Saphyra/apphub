package com.github.saphyra.apphub.api.notebook.model.table;

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
public class TableResponse {
    private String title;
    private UUID parent;
    private List<TableHeadModel> tableHeads;
    private List<TableRowModel> rows;
}
