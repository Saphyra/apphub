package com.github.saphyra.apphub.integration.structure.api.admin_panel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemoryStatusModel {
    private String service;
    private Long availableMemoryBytes;
    private Long allocatedMemoryBytes;
    private Long usedMemoryBytes;
    private Long epochSeconds;
}
