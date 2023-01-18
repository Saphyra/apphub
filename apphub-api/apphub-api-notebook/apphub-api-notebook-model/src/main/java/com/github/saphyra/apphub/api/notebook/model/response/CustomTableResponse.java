package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CustomTableResponse {
    private String title;
    private List<TableHeadResponse> tableHeads;
    private List<TableColumnResponse<Object>> tableColumns;
    private Map<Integer, Boolean> rowStatus;
}
