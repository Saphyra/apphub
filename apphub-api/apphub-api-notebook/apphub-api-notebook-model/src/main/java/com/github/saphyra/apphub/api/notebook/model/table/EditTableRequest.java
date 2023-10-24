package com.github.saphyra.apphub.api.notebook.model.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class EditTableRequest {
    private String title;
    private List<TableHeadModel> tableHeads;
    private List<TableRowModel> rows;
}
