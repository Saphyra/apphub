package com.github.saphyra.apphub.integration.backend.model.notebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class CreateCategoryRequest {
    private UUID parent;
    private String title;
}
