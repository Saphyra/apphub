package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
@Builder
public class ChecklistResponse {
    @NonNull
    private final String title;

    @NonNull
    private final List<ChecklistItemResponse> nodes;
}
