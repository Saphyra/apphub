package com.github.saphyra.apphub.integration.backend.model.notebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateTextRequest {
    private UUID parent;
    private String title;
    private String content;
}
