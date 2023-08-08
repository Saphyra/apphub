package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Data
@Builder
public class ChecklistResponse {
    @NonNull
    private final String title;

    private final UUID parent;

    @NonNull
    private final List<ChecklistItemResponse> nodes;
}
