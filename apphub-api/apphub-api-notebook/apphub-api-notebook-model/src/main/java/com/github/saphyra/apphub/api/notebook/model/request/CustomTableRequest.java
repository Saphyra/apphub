package com.github.saphyra.apphub.api.notebook.model.request;

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
public class CustomTableRequest {
    private String title;
    private UUID parent;
    private List<String> columnNames;
    private List<CustomTableRowRequest> rows;
}
