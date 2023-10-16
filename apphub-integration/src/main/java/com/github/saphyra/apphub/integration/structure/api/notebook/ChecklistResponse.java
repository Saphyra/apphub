package com.github.saphyra.apphub.integration.structure.api.notebook;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChecklistResponse {
    private String title;
    private UUID parent;
    private List<ChecklistItemModel> items;
}
