package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TableResponse<T> {
    @NonNull
    private final String title;

    private final UUID parent;

    @NonNull
    private final List<TableHeadResponse> tableHeads;

    @NonNull
    private final List<TableColumnResponse<T>> tableColumns;
}
