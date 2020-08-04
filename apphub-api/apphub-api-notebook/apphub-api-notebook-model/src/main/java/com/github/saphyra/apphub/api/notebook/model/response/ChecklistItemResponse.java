package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Data
@Builder
public class ChecklistItemResponse {
    @NonNull
    private final UUID checklistItemId;

    @NonNull
    private final String content;

    @NonNull
    private final Boolean checked;

    @NonNull
    private final Integer order;
}
