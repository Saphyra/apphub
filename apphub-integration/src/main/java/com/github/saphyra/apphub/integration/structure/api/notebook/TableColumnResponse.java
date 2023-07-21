package com.github.saphyra.apphub.integration.structure.api.notebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableColumnResponse {
    private UUID tableJoinId;
    private String content;
    private String type;
    private Integer rowIndex;
    private Integer columnIndex;
}
