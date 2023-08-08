package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ChecklistTableResponse {
    @NonNull
    private final String title;

    private final UUID parent;

    @NonNull
    private final List<TableHeadResponse> tableHeads;

    @NonNull
    private final List<TableColumnResponse<String>> tableColumns;

    @NonNull
    private final Map<Integer, ChecklistTableRowStatusResponse> rowStatus;
}
