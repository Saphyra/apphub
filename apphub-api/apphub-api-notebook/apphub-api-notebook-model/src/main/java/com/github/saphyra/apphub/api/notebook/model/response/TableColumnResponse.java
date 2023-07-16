package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@Builder
public class TableColumnResponse<T> {
    @NonNull
    private final UUID tableJoinId;

    @NonNull
    private final String type;

    private final T content;

    @NonNull
    private final Integer rowIndex;

    @NonNull
    private final Integer columnIndex;
}
