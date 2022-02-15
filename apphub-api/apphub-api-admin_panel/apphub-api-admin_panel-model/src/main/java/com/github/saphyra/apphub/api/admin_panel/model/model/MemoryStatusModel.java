package com.github.saphyra.apphub.api.admin_panel.model.model;

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
