package com.github.saphyra.apphub.integration.structure.notebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EditListItemRequest {
    private UUID parent;
    private String title;
    private String value;
}
