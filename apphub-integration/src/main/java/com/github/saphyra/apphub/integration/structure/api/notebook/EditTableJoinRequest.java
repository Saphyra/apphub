package com.github.saphyra.apphub.integration.structure.api.notebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EditTableJoinRequest {
    private UUID tableJoinId;
    private Integer rowIndex;
    private Integer columnIndex;
    private String content;
}
