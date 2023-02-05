package com.github.saphyra.apphub.api.notebook.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomTableColumnRequest {
    private Integer columnIndex;
    private String type;
    private Object value;
}
