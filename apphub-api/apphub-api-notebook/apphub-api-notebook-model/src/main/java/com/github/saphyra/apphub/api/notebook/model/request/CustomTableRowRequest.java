package com.github.saphyra.apphub.api.notebook.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomTableRowRequest {
    private Boolean checked;
    private List<CustomTableColumnRequest> columns;
}
