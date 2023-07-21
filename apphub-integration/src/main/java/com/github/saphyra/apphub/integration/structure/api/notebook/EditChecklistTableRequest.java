package com.github.saphyra.apphub.integration.structure.api.notebook;

import com.github.saphyra.apphub.integration.structure.api.KeyValuePair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EditChecklistTableRequest {
    private String title;
    private List<KeyValuePair<String>> columnNames;
    private List<ChecklistTableRowRequest<KeyValuePair<String>>> rows;
}
