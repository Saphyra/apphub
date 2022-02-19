package com.github.saphyra.apphub.integration.structure.notebook;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChecklistResponse {
    private String title;
    private List<ChecklistItemResponse> nodes;
}
