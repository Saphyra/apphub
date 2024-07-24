package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao;

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
    private final String brand;
    private final String name;
    private final Integer cost;
    private final LocalDate acquiredAt;
    private final LocalDate warrantyExpiresAt;
    private ToolStatus status;
    private LocalDate scrappedAt;
}
