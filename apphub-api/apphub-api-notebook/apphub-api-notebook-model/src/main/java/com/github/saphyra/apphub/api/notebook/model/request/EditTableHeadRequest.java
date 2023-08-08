package com.github.saphyra.apphub.api.notebook.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EditTableHeadRequest {
    private UUID tableHeadId;
    private Integer columnIndex;
    private String columnName;
}
