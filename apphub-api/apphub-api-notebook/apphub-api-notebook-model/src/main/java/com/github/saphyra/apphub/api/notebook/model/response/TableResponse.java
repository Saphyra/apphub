package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Builder
public class TableResponse<T> {
    @NonNull
    private final String title;

    @NonNull
    private final List<TableHeadResponse> tableHeads;

    @NonNull
    private final List<TableColumnResponse<T>> tableColumns;
}
