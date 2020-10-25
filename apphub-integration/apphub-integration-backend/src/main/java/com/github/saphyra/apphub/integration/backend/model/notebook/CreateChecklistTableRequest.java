package com.github.saphyra.apphub.integration.backend.model.notebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChecklistTableRequest {
    private String title;
    private UUID parent;
    private List<String> columnNames;
    private List<ChecklistTableRowRequest<String>> rows;
}
