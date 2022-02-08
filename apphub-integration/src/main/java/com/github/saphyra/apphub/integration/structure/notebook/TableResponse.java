package com.github.saphyra.apphub.integration.structure.notebook;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableResponse {
    private String title;
    private List<TableHeadResponse> tableHeads;
    private List<TableColumnResponse> tableColumns;
}
