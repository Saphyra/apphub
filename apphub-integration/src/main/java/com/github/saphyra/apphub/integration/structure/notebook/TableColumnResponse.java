package com.github.saphyra.apphub.integration.structure.notebook;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableColumnResponse {
    private UUID tableJoinId;
    private String content;
    private Integer rowIndex;
    private Integer columnIndex;
}
