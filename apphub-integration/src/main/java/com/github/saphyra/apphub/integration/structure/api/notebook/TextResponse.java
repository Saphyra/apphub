package com.github.saphyra.apphub.integration.structure.api.notebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TextResponse {
    private UUID textId;
    private UUID parent;
    private String title;
    private String content;
}
