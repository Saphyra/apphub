package com.github.saphyra.apphub.integration.structure.api.notebook.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EditTableRequest {
    private String title;
    private List<TableHeadModel> tableHeads;
    private List<TableRowModel> rows;
}
