package com.github.saphyra.apphub.integration.structure.notebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistTableResponse {
    private String title;
    private List<TableHeadResponse> tableHeads;
    private List<TableColumnResponse> tableColumns;
    private Map<Integer, Boolean> rowStatus;
}
