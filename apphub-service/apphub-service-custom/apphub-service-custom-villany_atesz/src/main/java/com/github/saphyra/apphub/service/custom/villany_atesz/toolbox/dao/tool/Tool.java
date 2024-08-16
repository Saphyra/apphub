package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.ToolStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class Tool {
    private final UUID toolId;
    private final UUID userId;
    private UUID storageBoxId;
    private UUID toolTypeId;
    private String brand;
    private String name;
    private Integer cost;
    private LocalDate acquiredAt;
    private LocalDate warrantyExpiresAt;
    private ToolStatus status;
    private LocalDate scrappedAt;
    private boolean inventoried;
}
