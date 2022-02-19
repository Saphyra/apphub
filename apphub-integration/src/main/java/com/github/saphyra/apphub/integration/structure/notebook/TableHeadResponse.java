package com.github.saphyra.apphub.integration.structure.notebook;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableHeadResponse {
    private UUID tableHeadId;
    private String content;
    private Integer columnIndex;
}
