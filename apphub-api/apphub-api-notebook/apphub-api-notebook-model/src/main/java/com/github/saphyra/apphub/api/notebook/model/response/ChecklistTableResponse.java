package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ChecklistTableResponse {
    @NonNull
    private final String title;

    @NonNull
    private final List<TableHeadResponse> tableHeads;

    @NonNull
    private final List<TableColumnResponse> tableColumns;

    @NonNull
    private final Map<Integer, Boolean> rowStatus;
}
