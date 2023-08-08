package com.github.saphyra.apphub.integration.structure.api.notebook;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ChecklistTableRow {
    private boolean checked;
    private List<String> columns;
}
