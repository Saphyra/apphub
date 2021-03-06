package com.github.saphyra.apphub.integration.backend.model.notebook;

import lombok.*;

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
