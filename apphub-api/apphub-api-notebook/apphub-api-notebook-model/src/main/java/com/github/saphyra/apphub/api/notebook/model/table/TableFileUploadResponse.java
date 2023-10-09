package com.github.saphyra.apphub.api.notebook.model.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class TableFileUploadResponse {
    private Integer rowIndex;
    private Integer columnIndex;
    private UUID storedFileId;
}
