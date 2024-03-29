package com.github.saphyra.apphub.integration.structure.api.notebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotebookView {
    private UUID id;
    private String title;
    private String type;
    private String value;
    private boolean pinned;
    private boolean archived;
    private UUID parentId;
    private String parentTitle;
    private boolean enabled;
}
