package com.github.saphyra.apphub.integration.structure.api.notebook;

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
    private List<TableHeadResponse> tableHeads;
    private List<TableColumnResponse> tableColumns;
}
