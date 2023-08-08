package com.github.saphyra.apphub.api.notebook.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistTableRowStatusResponse {
    private UUID rowId;
    private Boolean checked;
}
