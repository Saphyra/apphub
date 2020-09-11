package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@Builder
public class TableHeadResponse {
    @NonNull
    private final UUID tableHeadId;

    @NonNull
    private final String content;

    @NonNull
    private final Integer columnIndex;
}
